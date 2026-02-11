CREATE TYPE report_reason AS ENUM (
    'ai_generated',
    'self_promotion',
    'spam',
    'off_topic_content',
    'copyright_violation',
    'security_risk',
    'misinformation',
    'inappropriate_language',
    'other'
);

CREATE TABLE reports (
    id bigserial PRIMARY KEY,
    reported_by bigint NOT NULL REFERENCES accounts(id),
    reason report_reason NOT NULL,
    description text, -- optional
    created_at timestamp NOT NULL,
    resolved_by bigint REFERENCES accounts(id)
);

create table post_reports (
    id bigint primary key REFERENCES reports(id),
    post_id bigint not null REFERENCES posts(id)
);

create table comment_reports (
    id bigint primary key REFERENCES reports(id),
    comment_id bigint not null REFERENCES comments(id)
);

create table user_reports (
    id bigint primary key REFERENCES reports(id),
    user_id bigint not null REFERENCES users(account_id)
);