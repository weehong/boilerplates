import type { Metadata } from "next";
import Link from "next/link";

// Next.js automatically injects <meta name="robots" content="noindex"> for
// pages that return a 404 status.
export const metadata: Metadata = {
	title: "404 – Page not found",
	description: "The page you are looking for does not exist.",
};

export default function NotFound(): React.ReactElement {
	return (
		<div className="flex flex-1 flex-col items-center justify-center gap-4 p-16 text-center">
			<h1 className="text-4xl font-semibold tracking-tight">404</h1>
			<p className="text-zinc-600 dark:text-zinc-400">
				The page you are looking for does not exist.
			</p>
			<Link
				href="/"
				className="font-medium text-zinc-950 underline dark:text-zinc-50"
			>
				Go back home
			</Link>
		</div>
	);
}
