CREATE TABLE "zwhAggregate"
(
    date integer NOT NULL,
    corporation text NOT NULL,
    kills integer NOT NULL,
    isk double precision NOT NULL,
    active text NOT NULL,
    numactive integer NOT NULL,
    netisk double precision NOT NULL,
    sumonkills integer NOT NULL,
    CONSTRAINT "pk_zwhAggregate" PRIMARY KEY (corporation, date)
)