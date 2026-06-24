import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";
import tsconfigPaths from "vite-tsconfig-paths";

// https://nextjs.org/docs/app/guides/testing/vitest
export default defineConfig({
	plugins: [tsconfigPaths(), react()],
	test: {
		environment: "jsdom",
		globals: true,
		setupFiles: ["./vitest.setup.ts"],
		css: true,
		// Unit-test scope only; E2E lives in ./e2e and runs under Playwright.
		include: ["{app,components,lib,__tests__}/**/*.{test,spec}.{ts,tsx}"],
	},
});
