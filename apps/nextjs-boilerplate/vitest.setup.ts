import { afterEach } from "vitest";
import { cleanup } from "@testing-library/react";

// Registers @testing-library/jest-dom matchers with Vitest's `expect`.
import "@testing-library/jest-dom/vitest";

// runs a cleanup after each test case (e.g. clearing jsdom)
afterEach(() => {
	cleanup();
});
