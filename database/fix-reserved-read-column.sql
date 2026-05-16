-- Only run this IF tables already exist with a column named `read` (old schema).
-- If you get "Table doesn't exist", use init-schema.sql instead, or start the Spring Boot backend.

USE hirenest;

-- Uncomment only if notifications table exists AND has column `read`:
-- ALTER TABLE notifications CHANGE COLUMN `read` is_read TINYINT(1) NOT NULL DEFAULT 0;

-- Uncomment only if messages table exists AND has column `read`:
-- ALTER TABLE messages CHANGE COLUMN `read` is_read TINYINT(1) NOT NULL DEFAULT 0;
