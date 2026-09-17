-- Kalaza Leads — gate self-service signup by the active staff roster.
--
-- Problem: SupabaseAuthRepository.login() previously fell back to creating
-- a brand-new account for ANY name/password that failed sign-in -- meaning
-- literally anyone with the APK could get in and see every family's
-- medical/financial data, since RLS everywhere just checks
-- `auth.uid() is not null`.
--
-- Fix: before creating an account, the app checks this function to confirm
-- the typed name matches an active row in `staff`. It has to be callable
-- BEFORE a session exists (the user isn't authenticated yet at this point),
-- so a plain RLS-gated SELECT on `staff` won't work. A SECURITY DEFINER
-- function that returns only a boolean -- never the actual roster -- lets
-- an anonymous caller check one name at a time without exposing the full
-- staff list.

create or replace function public.is_active_staff_name(check_name text)
returns boolean
language sql
security definer
set search_path = public
as $$
  select exists (
    select 1 from public.staff
    where lower(trim(name)) = lower(trim(check_name))
      and is_active = true
  );
$$;

grant execute on function public.is_active_staff_name(text) to anon, authenticated;
