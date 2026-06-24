.PHONY: up down dev dev-down setup-env

# Seed local env files from the committed examples (no-clobber) so a fresh
# checkout can run `make up` without manual setup.
setup-env:
	@test -f .env.docker || cp .env.example .env.docker

up: setup-env
	docker compose --env-file .env.docker up -d

down:
	docker compose --env-file .env.docker down

dev: up

dev-down: down
