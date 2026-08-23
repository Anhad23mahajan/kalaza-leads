-- Kalaza Leads — leads table (docs/PROJECT_SPEC.md section 7)
-- One row per enquiry.
--
-- Also required in this Supabase project's Auth settings (Authentication > Sign In /
-- Providers > User Signups): "Confirm email" must stay OFF. Staff log in with a
-- synthesized email (staffname@kalazaleads.app, see SupabaseAuthRepository.kt) that
-- has no real inbox behind it — with email confirmation on, signup succeeds but the
-- account is stuck unconfirmed forever since the confirmation email goes nowhere.

create table public.leads (
  id                     uuid primary key default gen_random_uuid(),
  created_at             timestamptz not null default now(),
  assigned_staff_id      uuid references auth.users(id),

  source_channel         text check (source_channel in ('WHATSAPP', 'CALL', 'WALK_IN')),
  how_heard              text check (how_heard in ('SOCIAL_MEDIA', 'GOOGLE', 'REFERRAL', 'OTHER')),

  enquirer_name          text not null,
  enquirer_phone         text not null,
  enquirer_relation      text,

  patient_name           text,
  patient_age            int,

  location               text,
  service_wanted         text check (service_wanted in ('ASSISTED_LIVING', 'PALLIATIVE', 'OTHER')),
  room_preference        text check (room_preference in ('SINGLE', 'DOUBLE', 'SHARING')),
  budget                 numeric,

  medical_history        text,
  specific_requirements  text,

  status                 text not null default 'NEW'
                         check (status in ('NEW', 'CONTACTED', 'VISITED', 'CONVERTED', 'NOT_INTERESTED')),
  next_follow_up_date    date
);

alter table public.leads enable row level security;

-- This project was created with "Automatically expose new tables" turned off
-- (deliberately, for tighter default security), which means new tables get no
-- role grants by default — RLS policies alone aren't enough; Postgres also
-- requires the base GRANT before PostgREST can touch the table at all.
grant select, insert, update, delete on public.leads to authenticated;

-- MVP policy: any authenticated (logged-in staff) user has full access.
-- There's no staff table yet to key a tighter is_active_staff() check off of
-- (see Kalaza Care's pattern) — every Supabase Auth account IS a staff account
-- right now, since nothing else signs up through this app. Tighten this once
-- a real staff/roles table exists.
create policy "Authenticated staff can read leads"
  on public.leads for select
  using (auth.uid() is not null);

create policy "Authenticated staff can insert leads"
  on public.leads for insert
  with check (auth.uid() is not null);

create policy "Authenticated staff can update leads"
  on public.leads for update
  using (auth.uid() is not null);

create policy "Authenticated staff can delete leads"
  on public.leads for delete
  using (auth.uid() is not null);
