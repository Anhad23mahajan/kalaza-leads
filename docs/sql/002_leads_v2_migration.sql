-- Kalaza Leads — leads table v2 migration (docs/MASTER_PLAN_V2.md Part 5)
-- Drops and recreates `leads` on the revised post-supervisor-review schema.
-- Safe to run: as of 2026-08-25 the live table only holds 2 throwaway test
-- rows from development testing, nothing real to preserve.

drop table if exists public.leads cascade;

create table public.leads (
  id                       uuid primary key default gen_random_uuid(),
  created_at               timestamptz not null default now(),
  updated_at               timestamptz not null default now(),

  -- IDENTITY & INTAKE
  enquiry_date             date not null default current_date,
  contact_channel          text check (contact_channel in (
                              'phone_call', 'whatsapp', 'walk_in', 'website', 'email', 'instagram_dm'
                            )),
  how_heard                text check (how_heard in (
                              'google_search', 'google_maps', 'instagram', 'facebook',
                              'referral_friend_family', 'referral_hospital', 'referral_doctor',
                              'passing_by', 'newspaper', 'other'
                            )),
  how_heard_detail         text,

  -- ENQUIRER
  enquirer_name            text not null,
  enquirer_country_code    text not null default '+91',
  enquirer_phone           text not null,
  enquirer_relation        text check (enquirer_relation in (
                              'son', 'daughter', 'spouse', 'sibling', 'grandchild',
                              'nephew_niece', 'friend', 'self', 'hospital_staff', 'other'
                            )),
  enquirer_location        text,

  -- PATIENT
  patient_name             text,
  patient_age              int,
  patient_gender           text check (patient_gender in ('male', 'female', 'other')),
  patient_conditions       text[] not null default '{}',
  patient_condition_notes  text,
  current_condition        text,
  medical_history          text,

  -- REQUIREMENT
  service_wanted           text[] not null default '{}',
  accommodation_type       text check (accommodation_type in (
                              'single_room', 'double_sharing', 'triple_sharing',
                              'full_flat', 'dormitory', 'not_sure'
                            )),
  budget_min               numeric,
  budget_max               numeric,
  budget_notes             text,
  amenities_requested      text[] not null default '{}',
  special_requirements     text,
  queries                  text,
  comments                 text,

  -- PIPELINE
  status                   text not null default 'NEW' check (status in (
                              'NEW', 'CONTACTED', 'INFO_SENT', 'VISIT_SCHEDULED', 'VISITED',
                              'CONSIDERING', 'CONVERTED', 'NOT_CONVERTED', 'DORMANT', 'BACKUP'
                            )),
  assigned_staff_id        uuid references auth.users(id),
  next_follow_up_date      date,
  follow_up_count          int not null default 0,
  price_list_shared        boolean not null default false,
  price_list_shared_at     timestamptz,
  info_packs_sent          text[] not null default '{}',
  planned_visit_date       date,
  actual_visit_date        date,
  converted_at             date,
  days_to_convert          int generated always as (
                              case when converted_at is not null
                                then (converted_at - enquiry_date)
                                else null
                              end
                            ) stored,

  -- OUTCOME
  not_converted_reason     text check (not_converted_reason in (
                              'budget_too_high', 'chose_another_facility', 'location_too_far',
                              'amenity_missing', 'service_not_offered', 'family_decided_home_care',
                              'patient_passed_away', 'decision_postponed', 'unreachable_no_response',
                              'unhappy_after_visit', 'other'
                            )),
  not_converted_detail     text,
  feedback_positive_themes text[] not null default '{}',
  feedback_negative_themes text[] not null default '{}',
  final_remarks            text,

  -- COMPLIANCE
  consent_given             boolean not null default false,
  opted_out                 boolean not null default false,
  preferred_language        text not null default 'en' check (preferred_language in ('en', 'hi', 'mr'))
);

-- keep updated_at current on every row change
create or replace function public.set_updated_at()
returns trigger as $$
begin
  new.updated_at = now();
  return new;
end;
$$ language plpgsql;

create trigger leads_set_updated_at
  before update on public.leads
  for each row
  execute function public.set_updated_at();

alter table public.leads enable row level security;

-- same MVP posture as v1: no staff/roles table yet, so any authenticated
-- account is trusted staff. Grants are required in addition to RLS since
-- this project was created with "Automatically expose new tables" off.
grant select, insert, update, delete on public.leads to authenticated;

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
