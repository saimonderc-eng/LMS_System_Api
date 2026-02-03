DELETE FROM public.keycloak_role
WHERE realm_id = 'a579175c-f0a2-4843-8c13-af01a1ec0304'
AND name IN ('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT');



INSERT INTO public.keycloak_role (id, client_role, name, description, realm_id)
VALUES
    (gen_random_uuid(), false, 'ROLE_ADMIN', 'Администратор системы', 'a579175c-f0a2-4843-8c13-af01a1ec0304'),
    (gen_random_uuid(), false, 'ROLE_TEACHER', 'Преподаватель', 'a579175c-f0a2-4843-8c13-af01a1ec0304'),
    (gen_random_uuid(), false, 'ROLE_STUDENT', 'Студент/Пользователь', 'a579175c-f0a2-4843-8c13-af01a1ec0304')