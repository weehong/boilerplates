// Type the environment variables this app reads so `process.env.X` dot-access
// is allowed under `noPropertyAccessFromIndexSignature` and Next.js keeps
// inlining the `NEXT_PUBLIC_` vars at build time.
declare namespace NodeJS {
	interface ProcessEnv {
		/** Absolute canonical site URL, e.g. https://example.com (no trailing slash). */
		readonly NEXT_PUBLIC_SITE_URL?: string;
		/** App environment label surfaced to the client. */
		readonly NEXT_PUBLIC_APP_ENVIRONMENT?: string;
	}
}
