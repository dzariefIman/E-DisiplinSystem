# E-Disiplin

School disciplinary record management system built with Java EE 7.

## Stack

- Java 1.8 / Jakarta EE
- GlassFish 4.1.1
- Apache Derby (Java DB)
- JSP + JSTL + EL
- JDBC (active record pattern — DAO embedded in model classes)

## Project Structure

```
src/java/
  model/          — User.java, Incident.java (CRUD + business logic)
  servlet/        — 9 servlets (auth, HEP, counselor flows)
  util/           — DatabaseUtil.java (connection helper)

web/
  homeAndAuth/    — index, login, register
  hepJsp/         — HEP dashboard, add discipline, records
  counselorJsp/   — counselor dashboard, schedule, records
  StoryboardFiles/— original HTML mockups (kept for reference)
  WEB-INF/        — web.xml (servlet mappings)
```

## Setup

1. Create Derby database `E-Disiplin` (user: `app`, password: `app`)
2. Run `setup/create-database.sql` to create tables and seed data
3. Deploy to GlassFish 4.1.1
4. Access at `http://localhost:8080/E-Disiplin/`
