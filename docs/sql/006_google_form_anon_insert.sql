-- Kalaza Leads — allow anonymous inserts into `leads` (Google Form intake bridge)
-- The enquirer now fills a public Google Form (no login), which POSTs into
-- this table via a Google Apps Script bridge using the app's own anon key.
-- That key has no Supabase Auth session attached, so the existing
-- "authenticated" grants/policies don't cover it -- this migration adds a
-- narrowly-scoped exception: anon may INSERT only. Select/update/delete
-- stay authenticated-only, exactly as before. Nothing else changes.
--
-- Known, accepted trade-off: the anon key is already embedded in the
-- built APK (BuildConfig.SUPABASE_ANON_KEY) and is not secret by design --
-- anyone could in principle call this insert directly, not just through
-- the form. That only lets someone create extra rows (spam), not read,
-- edit, or delete anything -- select/update/delete remain fully
-- restricted to authenticated staff. Acceptable at this project's scale;
-- revisit if abuse ever becomes a real problem (e.g. add a shared-secret
-- check in the Apps Script bridge, or move the insert behind a Supabase
-- Edge Function using the service-role key instead of a public policy).

grant insert on public.leads to anon;

create policy "Anonymous Google Form bridge can insert leads"
  on public.leads for insert
  to anon
  with check (true);
