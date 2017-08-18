-- :name create-config! :! :n
-- :doc creates a new config record
INSERT INTO config
(name, value)
VALUES (:name, :value)

-- :name update-config! :! :n
-- :doc update an existing config record
UPDATE config
SET value = :value
WHERE name = :name

-- :name get-config :? :1
-- :doc retrieve a config given the key.
SELECT * FROM config
WHERE name = :name

-- :name delete-config! :! :n
-- :doc delete a config given the id
DELETE FROM config
WHERE id = :id
