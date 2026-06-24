import type { Metadata, Viewport } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import Providers from "./providers";
import { JsonLd } from "@/components/json-ld";
import { isProductionEnv, siteConfig } from "@/lib/site-config";
import { getOrganizationSchema, getWebSiteSchema } from "@/lib/structured-data";
import "./globals.css";

const geistSans = Geist({
	variable: "--font-geist-sans",
	subsets: ["latin"],
	display: "swap",
});

const geistMono = Geist_Mono({
	variable: "--font-geist-mono",
	subsets: ["latin"],
	display: "swap",
});

export const metadata: Metadata = {
	metadataBase: new URL(siteConfig.url),
	title: {
		default: siteConfig.name,
		template: `%s | ${siteConfig.name}`,
	},
	description: siteConfig.description,
	applicationName: siteConfig.name,
	keywords: [...siteConfig.keywords],
	authors: [{ name: siteConfig.author }],
	creator: siteConfig.creator,
	alternates: {
		canonical: "/",
	},
	openGraph: {
		type: "website",
		url: "/",
		siteName: siteConfig.name,
		title: siteConfig.name,
		description: siteConfig.description,
		locale: siteConfig.locale,
	},
	twitter: {
		card: "summary_large_image",
		title: siteConfig.name,
		description: siteConfig.description,
		creator: siteConfig.twitterHandle,
	},
	// Indexing is allowed in production only; non-production emits noindex,nofollow.
	robots: isProductionEnv
		? {
				index: true,
				follow: true,
				googleBot: {
					index: true,
					follow: true,
					"max-image-preview": "large",
					"max-snippet": -1,
					"max-video-preview": -1,
				},
			}
		: { index: false, follow: false },
	manifest: "/manifest.webmanifest",
	appleWebApp: {
		capable: true,
		title: siteConfig.shortName,
		statusBarStyle: "default",
	},
	formatDetection: { telephone: false },
	// verification: { google: "your-google-site-verification-token" },
};

export const viewport: Viewport = {
	width: "device-width",
	initialScale: 1,
	colorScheme: "light dark",
	themeColor: [
		{
			media: "(prefers-color-scheme: light)",
			color: siteConfig.themeColor.light,
		},
		{
			media: "(prefers-color-scheme: dark)",
			color: siteConfig.themeColor.dark,
		},
	],
};

export default function RootLayout({
	children,
}: Readonly<{
	children: React.ReactNode;
}>): React.ReactElement {
	return (
		<html
			lang="en"
			className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
		>
			<body className="min-h-full flex flex-col">
				<JsonLd data={getWebSiteSchema()} />
				<JsonLd data={getOrganizationSchema()} />
				<Providers>{children}</Providers>
			</body>
		</html>
	);
}
