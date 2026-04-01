-- Normalize reserved-ish date-part columns for invoices and tutor payouts.
-- Target canonical columns:
--   - invoices.year_value, invoices.month_value
--   - tutor_payouts.year_value, tutor_payouts.month_value
--
-- Force-normalize behavior:
--   - If both old+new exist: copy old -> new (when new is null), then drop old.
--   - If only old exists: rename to new.
--   - If only new exists: no-op.

do $$
begin
  -- invoices.month -> invoices.month_value
  if exists (
    select 1 from information_schema.columns
    where table_schema = 'public' and table_name = 'invoices' and column_name = 'month'
  ) then
    if exists (
      select 1 from information_schema.columns
      where table_schema = 'public' and table_name = 'invoices' and column_name = 'month_value'
    ) then
      execute 'update invoices set month_value = coalesce(month_value, month)';
      execute 'alter table invoices drop column month';
    else
      execute 'alter table invoices rename column month to month_value';
    end if;
  end if;

  -- invoices.year -> invoices.year_value
  if exists (
    select 1 from information_schema.columns
    where table_schema = 'public' and table_name = 'invoices' and column_name = 'year'
  ) then
    if exists (
      select 1 from information_schema.columns
      where table_schema = 'public' and table_name = 'invoices' and column_name = 'year_value'
    ) then
      execute 'update invoices set year_value = coalesce(year_value, year)';
      execute 'alter table invoices drop column year';
    else
      execute 'alter table invoices rename column year to year_value';
    end if;
  end if;

  execute 'alter table invoices drop constraint if exists uk_student_invoice';
  execute 'alter table invoices add constraint uk_student_invoice unique (student_id, year_value, month_value)';

  -- tutor_payouts.month -> tutor_payouts.month_value
  if exists (
    select 1 from information_schema.columns
    where table_schema = 'public' and table_name = 'tutor_payouts' and column_name = 'month'
  ) then
    if exists (
      select 1 from information_schema.columns
      where table_schema = 'public' and table_name = 'tutor_payouts' and column_name = 'month_value'
    ) then
      execute 'update tutor_payouts set month_value = coalesce(month_value, month)';
      execute 'alter table tutor_payouts drop column month';
    else
      execute 'alter table tutor_payouts rename column month to month_value';
    end if;
  end if;

  -- tutor_payouts.year -> tutor_payouts.year_value
  if exists (
    select 1 from information_schema.columns
    where table_schema = 'public' and table_name = 'tutor_payouts' and column_name = 'year'
  ) then
    if exists (
      select 1 from information_schema.columns
      where table_schema = 'public' and table_name = 'tutor_payouts' and column_name = 'year_value'
    ) then
      execute 'update tutor_payouts set year_value = coalesce(year_value, year)';
      execute 'alter table tutor_payouts drop column year';
    else
      execute 'alter table tutor_payouts rename column year to year_value';
    end if;
  end if;

  execute 'alter table tutor_payouts drop constraint if exists uk_tutor_payout';
  execute 'alter table tutor_payouts add constraint uk_tutor_payout unique (tutor_id, year_value, month_value)';

  execute 'drop index if exists idx_payouts_year_month';
  execute 'create index if not exists idx_payouts_year_month on tutor_payouts(year_value, month_value)';
end $$;
