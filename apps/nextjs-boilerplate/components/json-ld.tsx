import type { Thing, WithContext } from "schema-dts";

type JsonLdProps = {
	readonly data: WithContext<Thing>;
};

/**
 * Renders a JSON-LD structured-data <script> tag.
 *
 * `<` is replaced with its unicode escape to prevent XSS via the serialized
 * payload, per the Next.js JSON-LD guide.
 */
export function JsonLd({ data }: JsonLdProps): React.ReactElement {
	return (
		<script
			type="application/ld+json"
			dangerouslySetInnerHTML={{
				__html: JSON.stringify(data).replace(/</g, "\\u003c"),
			}}
		/>
	);
}
