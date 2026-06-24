import { expect, test } from "vitest";
import { render, screen } from "@testing-library/react";
import Home from "../app/page";

// Note: Vitest can unit-test synchronous Server/Client Components. For `async`
// Server Components, prefer E2E tests (see ./e2e). Reference:
// https://nextjs.org/docs/app/guides/testing/vitest
test("Home page renders the getting-started heading", () => {
	render(<Home />);
	expect(
		screen.getByRole("heading", { level: 1, name: /to get started/i })
	).toBeDefined();
});
