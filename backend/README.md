## Migrations ([golang-migrate](https://github.com/golang-migrate/migrate)):

installation:

```bash
go get -u -d github.com/golang-migrate/migrate/cmd/migrate
```

create migration:

```bash
migrate create -ext=.sql -dir=migrations <migration_name>
```

run migrations:

```bash
migrate -path migrations -database 'postgres://srm@localhost:5432/01blog?sslmode=disable' up
```
