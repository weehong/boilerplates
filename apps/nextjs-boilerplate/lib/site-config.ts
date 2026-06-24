/**
 * Central SEO / site configuration. Single source of truth imported by the
 * metadata, robots, sitemap, manifest, OG-image and structured-data modules.
 *
 * Edit these values to brand the boilerplate.
 */

const DEFAULT_URL = "http://localhost:3000";

const appEnvironment = process.env.NEXT_PUBLIC_APP_ENVIRONMENT ?? "development";

/**
 * Search-engine indexing is enabled **only in production**. In every other
 * environment (development, preview, staging) crawlers are blocked via
 * `robots.txt` and pages emit `noindex, nofollow`, so non-production
 * deployments are never indexed.
 */
export const isProductionEnv: boolean = appEnvironment === "production";

export const siteConfig = {
	/** Full brand / site name — used as the default <title> and OG site name. */
	name: "Next.js Boilerplate",
	/** Short name for the web app manifest (home-screen label). */
	shortName: "Next App",
	/** Default meta description. */
	description:
		"A production-ready Next.js boilerplate with batteries-included tooling and full SEO support.",
	/** Absolute canonical origin (no trailing slash). */
	url: process.env.NEXT_PUBLIC_SITE_URL ?? DEFAULT_URL,
	/** Open Graph locale. */
	locale: "en_US",
	/** Twitter/X handle for `twitter:creator` / `twitter:site`. */
	twitterHandle: "@nextjs",
	/** Default keywords. */
	keywords: ["Next.js", "React", "TypeScript", "boilerplate", "SEO"],
	/** Author / creator attribution. */
	author: "Next.js Boilerplate",
	creator: "Next.js Boilerplate",
	/** Alt text for the default OG/Twitter image. */
	ogImageAlt: "Next.js Boilerplate",
	/** theme-color values, kept in sync with app/globals.css. */
	themeColor: {
		light: "#ffffff",
		dark: "#0a0a0a",
	},
} as const;

export type SiteConfig = typeof siteConfig;
