# Issue tracker: Linear

Issues and PRDs for this repo live in **Linear**. There is no Linear CLI or MCP server wired up in this environment, so all operations go through the Linear GraphQL API with `curl`.

## Prerequisites

- **`LINEAR_API_KEY`** — a Linear personal API key (Linear → Settings → Security & access → Personal API keys). Passed in the `Authorization` header **verbatim, with no `Bearer ` prefix**.

  If the env var is unset, the key is also kept on this machine at `~/.credentials`. It is filed under an unrelated project block, so read it narrowly rather than dumping the file — that file holds many other live secrets:

  ```sh
  grep -A3 'horaion:' ~/.credentials | grep 'api_key:' | awk '{print $2}'
  ```

  Never write the key into a file in this repo, and never echo it into command output.

If no key can be found by either route, stop and tell the user — do not attempt to write issues by any other route.

## Which team

Issue numbering in Linear is **per team**, not per project — the identifier prefix is the team key (`SBB-1`), and projects have no counter of their own. Pick the team by repository:

| Repo path | Linear team |
| --- | --- |
| `apps/spring-boot-boilerplate` | `SBB` — Spring Boot Boilerplate |
| anything else | no team yet — **ask the user** before creating one |

`LINEAR_TEAM_KEY` overrides this table when set. If a boilerplate has no team listed, ask rather than defaulting into a catch-all team.

**A newly created team starts with no labels.** Before applying any triage label on a fresh team, create the five roles from `docs/agents/triage-labels.md` with `issueLabelCreate`. A new team also gets Linear's stock workflow states, which may not match those used elsewhere in the workspace.

## Conventions

All calls POST to `https://api.linear.app/graphql`. The reusable shape:

```sh
curl -sS https://api.linear.app/graphql \
  -H "Authorization: $LINEAR_API_KEY" \
  -H "Content-Type: application/json" \
  --data @- <<'JSON'
{ "query": "...", "variables": { } }
JSON
```

Linear identifies issues two ways: the human-readable **identifier** (`ENG-123`, shown in the UI) and an internal **UUID** (`id`, required by most mutations). Resolve identifier → UUID before mutating.

### Discover team, states, and labels

Do this once per session and reuse the ids; they are stable.

```graphql
query Bootstrap($key: String!) {
  teams(filter: { key: { eq: $key } }) {
    nodes {
      id
      key
      name
      labels { nodes { id name } }
      states { nodes { id name type } }
    }
  }
}
```

`states[].type` is one of `triage`, `backlog`, `unstarted`, `started`, `completed`, `canceled` — use the `type`, not the display name, when you need "open" vs "closed".

### Create an issue

```graphql
mutation Create($input: IssueCreateInput!) {
  issueCreate(input: $input) {
    success
    issue { id identifier url title }
  }
}
```

Variables: `{"input": {"teamId": "<uuid>", "title": "...", "description": "...", "labelIds": ["<uuid>"]}}`. `description` is Markdown. Always report the returned `identifier` and `url` back to the user.

Build the JSON payload with a script rather than shell interpolation — spec bodies contain backticks, quotes, and `$`, which shell heredocs will mangle silently.

An issue's team is fixed at creation: **moving an issue to another team re-issues its identifier and breaks the old URL.** Confirm the team before creating, not after.

### Read an issue

By identifier:

```graphql
query Read($key: String!, $number: Float!) {
  issues(filter: { team: { key: { eq: $key } }, number: { eq: $number } }) {
    nodes {
      id identifier title description url
      state { name type }
      labels { nodes { name } }
      comments { nodes { body createdAt user { name } } }
      children { nodes { identifier title state { type } } }
      parent { identifier title }
    }
  }
}
```

### List issues for triage

```graphql
query List($key: String!, $label: String!) {
  issues(
    filter: {
      team: { key: { eq: $key } }
      state: { type: { nin: ["completed", "canceled"] } }
      labels: { name: { eq: $label } }
    }
    first: 50
  ) {
    nodes { id identifier title description labels { nodes { name } } }
  }
}
```

Drop the `labels` filter to list everything open. Linear also has a **native Triage inbox** (`state.type = "triage"`) — if the team has it enabled, treat those issues as the untriaged queue alongside the `needs-triage` label.

### Comment on an issue

```graphql
mutation Comment($issueId: String!, $body: String!) {
  commentCreate(input: { issueId: $issueId, body: $body }) { success }
}
```

### Apply / remove labels

Linear's `issueUpdate` **replaces** the whole label set, so read the current `labelIds` first and send the merged list. Prefer the additive/subtractive mutations when available:

```graphql
mutation AddLabel($id: String!, $labelId: String!) {
  issueAddLabel(id: $id, labelId: $labelId) { success }
}
mutation RemoveLabel($id: String!, $labelId: String!) {
  issueRemoveLabel(id: $id, labelId: $labelId) { success }
}
```

If a triage label doesn't exist on the team yet, create it once with `issueLabelCreate(input: { teamId, name, color })` rather than silently skipping the label.

### Close an issue

Move it to a state whose `type` is `completed` (or `canceled` for wontfix), leaving a comment first:

```graphql
mutation Close($id: String!, $stateId: String!) {
  issueUpdate(id: $id, input: { stateId: $stateId }) { success }
}
```

## Pull requests as a request surface

**PRs as a request surface: no.** _(Set to `yes` if this repo treats external GitHub PRs as feature requests; `/triage` reads this flag.)_

The code lives on GitHub (`git@github.com:weehong/boilerplates.git`) while issues live in Linear, so the two surfaces are separate by default. If flipped to `yes`, enumerate external PRs with `gh pr list --state open --json number,title,body,author,authorAssociation` (keeping only `CONTRIBUTOR`, `FIRST_TIME_CONTRIBUTOR`, `NONE`) and mirror each one into Linear as an issue that links back to the PR URL.

## When a skill says "publish to the issue tracker"

Create a Linear issue with `issueCreate` and report its `identifier` and `url`.

## When a skill says "fetch the relevant ticket"

Run the **Read an issue** query for that identifier, including comments and labels.

## Wayfinding operations

Used by `/wayfinder`. The **map** is a single issue with **sub-issues** as tickets.

- **Map**: an issue labelled `wayfinder:map`, holding the Notes / Decisions-so-far / Fog body.
- **Child ticket**: an issue created with `parentId` set to the map's UUID — Linear's native sub-issue relationship. Label it `wayfinder:<type>` (`research` / `prototype` / `grilling` / `task`). Once claimed, assign it to the driving dev.
- **Blocking**: Linear's native issue relations. `issueRelationCreate(input: { issueId: "<blocked>", relatedIssueId: "<blocker>", type: blocks })`. Read them back via `issue { relations { nodes { type relatedIssue { identifier state { type } } } } }`. A ticket is unblocked when every blocker's state `type` is `completed` or `canceled`.
- **Frontier query**: list the map's `children` that are still open, drop any with an unresolved blocking relation or an `assignee`; first in map order wins.
- **Claim**: `issueUpdate(id, input: { assigneeId: "<me>" })` — the session's first write. Get your own id with `query { viewer { id name } }`.
- **Resolve**: `commentCreate` with the answer, move the issue to a `completed` state, then append a context pointer to the map's Decisions-so-far.
