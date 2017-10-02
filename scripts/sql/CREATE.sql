CREATE TABLE "zwbAggregateCorp"
(
    date integer NOT NULL,
    corporationid integer NOT NULL,
    corporation text NOT NULL,
    kills integer NOT NULL,
    losses integer NOT NULL,
    iskwon double precision NOT NULL,
    isklost double precision NOT NULL,
    active text NOT NULL,
    numactive integer NOT NULL,
    sumonkills integer NOT NULL,
    killsinhour00 integer NOT NULL,
    killsinhour01 integer NOT NULL,
    killsinhour02 integer NOT NULL,
    killsinhour03 integer NOT NULL,
    killsinhour04 integer NOT NULL,
    killsinhour05 integer NOT NULL,
    killsinhour06 integer NOT NULL,
    killsinhour07 integer NOT NULL,
    killsinhour08 integer NOT NULL,
    killsinhour09 integer NOT NULL,
    killsinhour10 integer NOT NULL,
    killsinhour11 integer NOT NULL,
    killsinhour12 integer NOT NULL,
    killsinhour13 integer NOT NULL,
    killsinhour14 integer NOT NULL,
    killsinhour15 integer NOT NULL,
    killsinhour16 integer NOT NULL,
    killsinhour17 integer NOT NULL,
    killsinhour18 integer NOT NULL,
    killsinhour19 integer NOT NULL,
    killsinhour20 integer NOT NULL,
    killsinhour21 integer NOT NULL,
    killsinhour22 integer NOT NULL,
    killsinhour23 integer NOT NULL,
    sumonkillsinhour00 integer NOT NULL,
    sumonkillsinhour01 integer NOT NULL,
    sumonkillsinhour02 integer NOT NULL,
    sumonkillsinhour03 integer NOT NULL,
    sumonkillsinhour04 integer NOT NULL,
    sumonkillsinhour05 integer NOT NULL,
    sumonkillsinhour06 integer NOT NULL,
    sumonkillsinhour07 integer NOT NULL,
    sumonkillsinhour08 integer NOT NULL,
    sumonkillsinhour09 integer NOT NULL,
    sumonkillsinhour10 integer NOT NULL,
    sumonkillsinhour11 integer NOT NULL,
    sumonkillsinhour12 integer NOT NULL,
    sumonkillsinhour13 integer NOT NULL,
    sumonkillsinhour14 integer NOT NULL,
    sumonkillsinhour15 integer NOT NULL,
    sumonkillsinhour16 integer NOT NULL,
    sumonkillsinhour17 integer NOT NULL,
    sumonkillsinhour18 integer NOT NULL,
    sumonkillsinhour19 integer NOT NULL,
    sumonkillsinhour20 integer NOT NULL,
    sumonkillsinhour21 integer NOT NULL,
    sumonkillsinhour22 integer NOT NULL,
    sumonkillsinhour23 integer NOT NULL,
    CONSTRAINT "pk_zwbAggregateCorp" PRIMARY KEY (date, corporationid)
)

CREATE TABLE "zwbAggregateChar"
(
    date integer NOT NULL,
    characterid integer NOT NULL,
    character text NOT NULL,
    kills integer NOT NULL,
    losses integer NOT NULL,
    iskwon double precision NOT NULL,
    isklost double precision NOT NULL,
    CONSTRAINT "pk_zwbAggregateChar" PRIMARY KEY (date, characterid)
)

CREATE TABLE "zwbKnownCharacter"
(
    id integer NOT NULL,
    name varchar(255) NOT NULL,
    CONSTRAINT "pk_zwbKnownCharacter" PRIMARY KEY (id)
)

CREATE TABLE "zwbKnownCorporation"
(
    id integer NOT NULL,
    name varchar(255) NOT NULL,
    CONSTRAINT "pk_zwbKnownCorporation" PRIMARY KEY (id)
)