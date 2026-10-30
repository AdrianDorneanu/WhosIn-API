ALTER TABLE games
    ALTER COLUMN public_id TYPE UUID
        USING public_id::UUID;
