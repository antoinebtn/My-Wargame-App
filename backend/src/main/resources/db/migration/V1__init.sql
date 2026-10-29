create table app_user (
    id            bigint generated always as identity primary key,
    email         varchar(255) not null unique,
    password_hash varchar(100) not null,
    display_name  varchar(100) not null,
    created_at    timestamptz  not null default now()
);

-- Global paint catalogue, seeded from Arcturus5404/miniature-paints
create table paint_brand (
    id   bigint generated always as identity primary key,
    name varchar(100) not null,
    slug varchar(100) not null unique
);

create table paint (
    id        bigint generated always as identity primary key,
    brand_id  bigint       not null references paint_brand (id),
    name      varchar(200) not null,
    code      varchar(50),
    paint_set varchar(200),
    hex       varchar(7)   not null,
    r         integer      not null,
    g         integer      not null,
    b         integer      not null
);
create index paint_brand_idx on paint (brand_id);
create index paint_name_idx on paint (lower(name));

-- Paint pots owned by a user
create table user_paint (
    id         bigint generated always as identity primary key,
    user_id    bigint      not null references app_user (id) on delete cascade,
    paint_id   bigint      not null references paint (id),
    quantity   integer     not null default 1,
    level      varchar(20) not null default 'FULL',
    notes      text,
    created_at timestamptz not null default now(),
    unique (user_id, paint_id)
);

create table army (
    id          bigint generated always as identity primary key,
    user_id     bigint       not null references app_user (id) on delete cascade,
    name        varchar(150) not null,
    faction     varchar(150),
    game_system varchar(150),
    description text,
    created_at  timestamptz  not null default now()
);
create index army_user_idx on army (user_id);

create table army_unit (
    id      bigint generated always as identity primary key,
    army_id bigint       not null references army (id) on delete cascade,
    name    varchar(150) not null,
    notes   text
);
create index army_unit_army_idx on army_unit (army_id);

create table miniature (
    id      bigint generated always as identity primary key,
    unit_id bigint      not null references army_unit (id) on delete cascade,
    name    varchar(150) not null,
    stage   varchar(20) not null default 'UNASSEMBLED'
);
create index miniature_unit_idx on miniature (unit_id);
