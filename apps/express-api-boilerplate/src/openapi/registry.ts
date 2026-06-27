import {
	extendZodWithOpenApi,
	OpenAPIRegistry,
} from "@asteasolutions/zod-to-openapi";
import { z } from "zod";

// Teach zod about `.openapi()` metadata. Must run before any schema uses it.
extendZodWithOpenApi(z);

export const registry: OpenAPIRegistry = new OpenAPIRegistry();

const HealthSchema = registry.register(
	"HealthStatus",
	z.object({
		status: z.literal("ok"),
		uptime: z.number().openapi({ example: 12.34 }),
		timestamp: z.string().datetime(),
		version: z.string().openapi({ example: "0.0.0" }),
	})
);

const ReadinessSchema = registry.register(
	"ReadinessStatus",
	z.object({
		status: z.enum(["ready", "not_ready"]),
		checks: z.object({
			database: z.enum(["up", "down"]),
		}),
	})
);

function dataEnvelope(schema: z.ZodTypeAny): z.ZodTypeAny {
	return z.object({ data: schema });
}

registry.registerPath({
	method: "get",
	path: "/health",
	summary: "Liveness probe",
	tags: ["Health"],
	responses: {
		200: {
			description: "The service is up.",
			content: {
				"application/json": { schema: dataEnvelope(HealthSchema) },
			},
		},
	},
});

registry.registerPath({
	method: "get",
	path: "/ready",
	summary: "Readiness probe",
	tags: ["Health"],
	responses: {
		200: {
			description: "The service and its dependencies are ready.",
			content: {
				"application/json": { schema: dataEnvelope(ReadinessSchema) },
			},
		},
		503: {
			description: "A dependency is unavailable.",
			content: {
				"application/json": { schema: dataEnvelope(ReadinessSchema) },
			},
		},
	},
});
