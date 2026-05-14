ALTER TABLE comments
ADD COLUMN created_at timestamp;

UPDATE comments
SET created_at = now()
WHERE created_at IS NULL;

ALTER TABLE comments
ALTER COLUMN created_at SET NOT NULL;