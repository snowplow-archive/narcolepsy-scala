DROP TABLE "public"."platform_instance_parameters";

DROP TABLE "public"."platform_instances";

DROP TABLE "public"."platforms";

CREATE TABLE "public"."platforms" (
"id" int NOT NULL UNIQUE,
"platform_name" varchar(256) NOT NULL,
"platform_connector" varchar(256) NOT NULL
)
WITH (OIDS=FALSE);

INSERT INTO "public"."platforms" VALUES ('1', 'PrestaShop', 'PrestaShopConnector');
INSERT INTO "public"."platforms" VALUES ('2', 'Amazon Marketplace', 'AmazonMarketplaceConnector');

CREATE TABLE "public"."platform_instances" (
"id" int NOT NULL UNIQUE,
"platform_instance_name" varchar(256) NOT NULL,
"platform_id" integer NOT NULL REFERENCES "public"."platforms"("id"),
"poll_frequency" integer NULL
)
WITH (OIDS=FALSE);

INSERT INTO "public"."platform_instances" VALUES ('1', 'Psychic Bazaar PrestaShop', '1', '15');
INSERT INTO "public"."platform_instances" VALUES ('2', 'Psychic Bazaar Amazon Marketplace UK', '2', '60');


CREATE TABLE "public"."platform_instance_parameters" (
"id" int NOT NULL UNIQUE,
"platform_instance_id" integer NOT NULL REFERENCES "public"."platform_instances"("id"),
"parameter_name" varchar(128) NOT NULL,
"parameter_value" varchar(128)
)
WITH (OIDS=FALSE);

INSERT INTO "public"."platform_instance_parameters" VALUES ('1', '1', 'api_key', 'R1R6NC8GB3CPXDPNFZ87N7T8LQ30SRH1');
INSERT INTO "public"."platform_instance_parameters" VALUES ('2', '1', 'api_url', 'http://test.psychicbazaar.com/api');
INSERT INTO "public"."platform_instance_parameters" VALUES ('3', '2', 'access_key_id', 'AKIAIDNIJIVSRTI5ATLQ');
INSERT INTO "public"."platform_instance_parameters" VALUES ('4', '2', 'secret_access_key', '9uldEMo0CVnrFkHH52ic');
INSERT INTO "public"."platform_instance_parameters" VALUES ('5', '2', 'merchant_id', 'A29MOI9NSKCQ2V');
INSERT INTO "public"."platform_instance_parameters" VALUES ('6', '2', 'marketplace_id', 'A1F83G8C2ARO7P');
INSERT INTO "public"."platform_instance_parameters" VALUES ('7', '2', 'marketplace_locale', 'GB');

CREATE SEQUENCE "public"."platforms_id_seq";

CREATE SEQUENCE "public"."platform_instances_id_seq";

CREATE SEQUENCE "public"."platform_instance_parameters_id_seq";

ALTER TABLE "public"."platforms" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."platforms"
    ALTER COLUMN "id"
        SET DEFAULT NEXTVAL('platforms_id_seq');

ALTER TABLE "public"."platform_instances" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."platform_instances"
    ALTER COLUMN "id"
        SET DEFAULT NEXTVAL('platform_instances_id_seq');

ALTER TABLE "public"."platform_instance_parameters" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."platform_instance_parameters"
    ALTER COLUMN "id"
        SET DEFAULT NEXTVAL('platform_instance_parameters_id_seq');