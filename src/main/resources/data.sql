-- Sample data for testing the CRUD API
INSERT INTO users (name, email, bio, created_at, updated_at) VALUES
('John Doe', 'john.doe@example.com', 'Software Engineer passionate about Java and Spring Boot', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jane Smith', 'jane.smith@example.com', 'DevOps Engineer specializing in observability and monitoring', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bob Johnson', 'bob.johnson@example.com', 'Full Stack Developer with expertise in microservices', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Alice Brown', 'alice.brown@example.com', 'Data Scientist working on machine learning projects', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Charlie Wilson', 'charlie.wilson@example.com', 'Cloud Architect focused on AWS and containerization', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 