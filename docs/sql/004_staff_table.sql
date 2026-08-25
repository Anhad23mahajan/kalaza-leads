-- Kalaza Leads — staff table (docs/MASTER_PLAN_V2.md Part 5.4, roadmap item A8)
-- A roster of staff for assignment ("follow-up person" in the supervisor's
-- Excel) and basic roles. Deliberately not tied 1:1 to auth.users -- not
-- every staff member necessarily has an app login yet.

create table public.staff (
  id         uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now(),
  name       text not null,
  phone      text,
  role       text not null default 'coordinator' check (role in ('admin', 'coordinator', 'viewer')),
  is_active  boolean not null default true
);

alter table public.staff enable row level security;

-- same MVP posture as leads/contact_activities: any authenticated account is trusted staff.
grant select, insert, update, delete on public.staff to authenticated;

create policy "Authenticated staff can read staff"
  on public.staff for select
  using (auth.uid() is not null);

create policy "Authenticated staff can insert staff"
  on public.staff for insert
  with check (auth.uid() is not null);

create policy "Authenticated staff can update staff"
  on public.staff for update
  using (auth.uid() is not null);

create policy "Authenticated staff can delete staff"
  on public.staff for delete
  using (auth.uid() is not null);

-- leads.assigned_staff_id was pointed at auth.users(id) in the 002 migration,
-- written before this staff directory existed. Repoint it at staff(id) --
-- assignment is to a roster entry, not necessarily someone with an app login.
-- Safe: the assignment UI never shipped, so the column is null on every row.
alter table public.leads drop constraint if exists leads_assigned_staff_id_fkey;
alter table public.leads add constraint leads_assigned_staff_id_fkey
  foreign key (assigned_staff_id) references public.staff(id);
