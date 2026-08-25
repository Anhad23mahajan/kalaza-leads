-- Kalaza Leads — contact_activities table (docs/MASTER_PLAN_V2.md Part 5.4)
-- The "proof" log: every call, WhatsApp message, and visit for a lead,
-- with outcome and notes -- the feature the NGO supervisor explicitly asked for.

create table public.contact_activities (
  id             uuid primary key default gen_random_uuid(),
  lead_id        uuid not null references public.leads(id) on delete cascade,
  occurred_at    timestamptz not null default now(),
  type           text not null check (type in ('call', 'whatsapp', 'visit', 'email', 'sms')),
  direction      text not null check (direction in ('inbound', 'outbound')),
  outcome        text check (outcome in (
                    'positive', 'negative', 'no_answer', 'callback_requested', 'not_reachable'
                  )),
  callback_on    date,
  notes          text,
  staff_id       uuid references auth.users(id),
  is_automated   boolean not null default false
);

create index contact_activities_lead_id_idx on public.contact_activities(lead_id);

alter table public.contact_activities enable row level security;

-- same MVP posture as leads: any authenticated account is trusted staff.
grant select, insert, update, delete on public.contact_activities to authenticated;

create policy "Authenticated staff can read contact activities"
  on public.contact_activities for select
  using (auth.uid() is not null);

create policy "Authenticated staff can insert contact activities"
  on public.contact_activities for insert
  with check (auth.uid() is not null);

create policy "Authenticated staff can update contact activities"
  on public.contact_activities for update
  using (auth.uid() is not null);

create policy "Authenticated staff can delete contact activities"
  on public.contact_activities for delete
  using (auth.uid() is not null);
