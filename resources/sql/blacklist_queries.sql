-- :name get-all-entries! :? :*
-- :doc creates a new blacklist record
SELECT
  id,
  concat(inet6_ntoa(ip), '/', prefix) AS ipaddr,
  type,
  date,
  description
FROM blacklist;

-- :name create-entry! :! :n
-- :doc creates a new blacklist record
INSERT INTO blacklist
(ip, prefix, type, date, description)
VALUES (INET6_ATON(:ip), :prefix, :type, :date, :description);

-- :name update-entry! :! :n
-- :doc update an existing blacklist record
UPDATE blacklist
SET ip        = INET6_ATON(:ip),
  prefix      = :prefix,
  type        = :type,
  date        = :date,
  description = :description
WHERE id = :id;

-- :name get-entry :? :1
-- :doc retrieve a blacklist entry given the id.
SELECT
  id,
  concat(inet6_ntoa(ip), '/', prefix) AS ipaddr,
  type,
  date,
  description
FROM blacklist
WHERE id = :id;

-- :name delete-entry! :! :n
-- :doc delete a blacklist entry given the id
DELETE FROM blacklist
WHERE id = :id;
