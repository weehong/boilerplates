# syntax=docker/dockerfile:1
# Multi-stage build for the Express API. Produces a slim, non-root runtime image.

FROM node:22-alpine AS base

# --- Install dependencies only when needed ---
FROM base AS deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

# --- Build the application ---
FROM base AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npx prisma generate
RUN npm run build

# --- Production runner ---
FROM base AS runner
WORKDIR /app
ENV NODE_ENV=production

COPY package.json package-lock.json ./
RUN npm ci --omit=dev

RUN addgroup --system --gid 1001 nodejs \
	&& adduser --system --uid 1001 nodeapp

# Reuse the Prisma client generated in the builder (the `prisma` CLI is a
# devDependency and is intentionally absent from the production image).
COPY --from=builder /app/prisma ./prisma
COPY --from=builder /app/node_modules/.prisma ./node_modules/.prisma
COPY --from=builder --chown=nodeapp:nodejs /app/dist ./dist

USER nodeapp
EXPOSE 3000
ENV PORT=3000

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
	CMD node -e "fetch('http://localhost:'+(process.env.PORT||3000)+'/health').then(r=>process.exit(r.ok?0:1)).catch(()=>process.exit(1))"

CMD ["node", "dist/server.js"]
