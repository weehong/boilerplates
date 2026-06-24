import { test, expect } from "@playwright/test";

test("home page renders the getting-started heading", async ({ page }) => {
	// baseURL is set via `use.baseURL` in playwright.config.ts
	await page.goto("/");
	await expect(
		page.getByRole("heading", { level: 1, name: /to get started/i })
	).toBeVisible();
});
