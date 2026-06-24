"use client";

import {
	isServer,
	QueryClient,
	QueryClientProvider,
} from "@tanstack/react-query";

function makeQueryClient(): QueryClient {
	return new QueryClient({
		defaultOptions: {
			queries: {
				// With SSR, avoid refetching immediately on the client.
				staleTime: 60 * 1000,
			},
		},
	});
}

let browserQueryClient: QueryClient | undefined = undefined;

function getQueryClient(): QueryClient {
	if (isServer) {
		// Server: always make a new query client per request.
		return makeQueryClient();
	}
	// Browser: reuse a single client across renders.
	browserQueryClient ??= makeQueryClient();
	return browserQueryClient;
}

export default function Providers({
	children,
}: Readonly<{
	children: React.ReactNode;
}>): React.ReactElement {
	const queryClient = getQueryClient();

	return (
		<QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
	);
}
