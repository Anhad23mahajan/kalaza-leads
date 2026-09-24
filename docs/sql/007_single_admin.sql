-- Kalaza Leads -- single-admin simplification (2026-09-24)
--
-- The app is used by exactly ONE person (the admin), so the multi-user machinery is removed:
--   * the staff roster (`staff` table) and its Staff screen
--   * the signup gate function is_active_staff_name (signup no longer exists in the app)
--   * lead assignment (leads.assigned_staff_id) -- there is nobody to assign to
--
-- ORDER OF OPERATIONS (important):
--   1. Run this migration FIRST (Supabase -> SQL Editor).
--   2. THEN build and install the new APK. (The new app no longer knows the dropped
--      column. If the old APK is still installed in between, only its Save-edit and
--      Staff screen break -- reading leads keeps working.)
--   3. Supabase dashboard -> Authentication -> Sign In / Providers -> turn OFF
--      "Allow new users to sign up". The admin account already exists; nobody else
--      may ever create one. (On a fresh project: create the admin under
--      Authentication -> Users -> Add user, email = <name lowercased, spaces -> _>@kalazaleads.app,
--      set a password, tick "Auto Confirm User", THEN disable signups.)
--
-- Destructive: dropping the column/table deletes any assignment data. It was never used
-- for anything but the (now-removed) "Assigned to" dropdown.

drop function if exists public.is_active_staff_name(text);

alter table public.leads drop constraint if exists leads_assigned_staff_id_fkey;
alter table public.leads drop column if exists assigned_staff_id;

drop table if exists public.staff;
