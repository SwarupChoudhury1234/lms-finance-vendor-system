CREATE TABLE fee_types (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- 2. FEE STRUCTURES (Course-wise)
-- ============================================
CREATE TABLE fee_structures (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fee_type_id BIGINT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    course_id BIGINT NOT NULL,
    batch_id BIGINT,
    total_amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'INR',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (fee_type_id) REFERENCES fee_types(id),
    UNIQUE KEY unique_fee_structure (fee_type_id, academic_year, course_id, batch_id)
);

-- ============================================
-- 3. FEE DISCOUNTS (Scholarships)
-- ============================================
CREATE TABLE fee_discounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    fee_structure_id BIGINT NOT NULL,
    discount_name VARCHAR(100),
    discount_type ENUM('PERCENTAGE', 'FLAT') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    reason VARCHAR(255),
    approved_by BIGINT,
    approved_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (fee_structure_id) REFERENCES fee_structures(id)
);

-- ============================================
-- 4. STUDENT FEE ALLOCATIONS (Main Fee Record)
-- ============================================
CREATE TABLE student_fee_allocations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    fee_structure_id BIGINT NOT NULL,
    original_amount DECIMAL(12,2) NOT NULL,
    total_discount DECIMAL(12,2) DEFAULT 0,
    payable_amount DECIMAL(12,2) NOT NULL,
    advance_payment DECIMAL(12,2) DEFAULT 0,
    remaining_amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'INR',
    allocation_date DATE DEFAULT (CURRENT_DATE),
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED', 'REFUNDED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (fee_structure_id) REFERENCES fee_structures(id)
);

-- ============================================
-- 5. PAYMENT ALTERNATIVES (Admin Config)
-- ============================================
CREATE TABLE payment_alternatives (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    alternative_name VARCHAR(100) NOT NULL,
    number_of_installments INT NOT NULL,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- 6. STUDENT INSTALLMENT PLANS (Student Choice)
-- ============================================
CREATE TABLE student_installment_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_fee_allocation_id BIGINT NOT NULL,
    payment_alternative_id BIGINT,  -- 🔴 UPDATED: Removed NOT NULL to allow Ad-Hoc plans
    installment_number INT NOT NULL,
    installment_amount DECIMAL(12,2) NOT NULL,
    due_date DATE NOT NULL,
    paid_amount DECIMAL(12,2) DEFAULT 0,
    status ENUM('PENDING', 'PARTIALLY_PAID', 'PAID', 'OVERDUE') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_fee_allocation_id) REFERENCES student_fee_allocations(id),
    FOREIGN KEY (payment_alternative_id) REFERENCES payment_alternatives(id)
);

-- ============================================
-- 7. STUDENT FEE PAYMENTS (Actual Payments)
-- ============================================
CREATE TABLE student_fee_payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_fee_allocation_id BIGINT NOT NULL,
    student_installment_plan_id BIGINT,
    paid_amount DECIMAL(12,2) NOT NULL,
    payment_date DATETIME NOT NULL,
    payment_mode ENUM('CASH', 'CARD', 'UPI', 'NET_BANKING', 'BANK_TRANSFER', 'AUTO_DEBIT') NOT NULL,
    payment_status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    transaction_reference VARCHAR(100),
    gateway_response TEXT,
    screenshot_url VARCHAR(500),
    currency VARCHAR(10) DEFAULT 'INR',
    recorded_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_fee_allocation_id) REFERENCES student_fee_allocations(id),
    FOREIGN KEY (student_installment_plan_id) REFERENCES student_installment_plans(id)
);

-- ============================================
-- 8. LATE FEE CONFIGURATION (Admin Rules)
-- ============================================
CREATE TABLE late_fee_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_schedule ENUM('MONTHLY', 'QUARTERLY', 'YEARLY') NOT NULL,
    period_count INT NOT NULL COMMENT 'Every k months/quarters/years',
    penalty_amount DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    effective_from DATE NOT NULL,
    effective_to DATE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- 9. LATE FEE PENALTIES (Applied to Students)
-- ============================================
CREATE TABLE late_fee_penalties (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_installment_plan_id BIGINT NOT NULL,
    penalty_amount DECIMAL(10,2) NOT NULL,
    penalty_date DATE NOT NULL,
    reason VARCHAR(255),
    is_waived BOOLEAN DEFAULT FALSE,
    waived_by BIGINT,
    waived_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_installment_plan_id) REFERENCES student_installment_plans(id)
);

-- ============================================
-- 10. ATTENDANCE PENALTIES
-- ============================================
CREATE TABLE attendance_penalties (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    student_fee_allocation_id BIGINT NOT NULL,
    absence_date DATE NOT NULL,
    penalty_amount DECIMAL(10,2) NOT NULL,
    reason VARCHAR(255),
    applied_by BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_fee_allocation_id) REFERENCES student_fee_allocations(id)
);

-- ============================================
-- 11. EXAM FEE LINKAGE
-- ============================================
CREATE TABLE exam_fee_linkage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exam_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    student_fee_allocation_id BIGINT NOT NULL,
    exam_fee_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'INR',
    applied_date DATE DEFAULT (CURRENT_DATE),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_fee_allocation_id) REFERENCES student_fee_allocations(id)
);

-- ============================================
-- 12. FEE REFUNDS
-- ============================================
CREATE TABLE fee_refunds (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_fee_payment_id BIGINT NOT NULL,
    student_fee_allocation_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    refund_amount DECIMAL(12,2) NOT NULL,
    refund_reason VARCHAR(500),
    refund_status ENUM('PENDING', 'APPROVED', 'PROCESSED', 'REJECTED') DEFAULT 'PENDING',
    requested_date DATE NOT NULL,
    approved_by BIGINT,
    approved_date DATE,
    processed_date DATE,
    refund_mode VARCHAR(50),
    transaction_reference VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_fee_payment_id) REFERENCES student_fee_payments(id),
    FOREIGN KEY (student_fee_allocation_id) REFERENCES student_fee_allocations(id)
);

-- ============================================
-- 13. FEE RECEIPTS (Auto-generated)
-- ============================================
CREATE TABLE fee_receipts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    receipt_number VARCHAR(100) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    receipt_pdf_url VARCHAR(500),
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES student_fee_payments(id)
);

-- ============================================
-- 14. PAYMENT NOTIFICATIONS
-- ============================================
CREATE TABLE payment_notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    notification_type ENUM('PAYMENT_SUCCESS', 'PAYMENT_FAILED', 'DUE_REMINDER', 'OVERDUE_WARNING', 'RECEIPT_SENT') NOT NULL,
    message TEXT NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_status ENUM('PENDING', 'SENT', 'FAILED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 15. AUTO DEBIT CONFIGURATION
-- ============================================
CREATE TABLE auto_debit_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    student_fee_allocation_id BIGINT NOT NULL,
    bank_account_number VARCHAR(50),
    card_token VARCHAR(255),
    payment_gateway VARCHAR(50),
    auto_debit_day INT COMMENT 'Day of month for auto-debit',
    is_active BOOLEAN DEFAULT TRUE,
    consent_given BOOLEAN DEFAULT FALSE,
    consent_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_fee_allocation_id) REFERENCES student_fee_allocations(id)
);

-- ============================================
-- 16. CURRENCY RATES
-- ============================================
CREATE TABLE currency_rates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    from_currency VARCHAR(10) NOT NULL,
    to_currency VARCHAR(10) NOT NULL,
    exchange_rate DECIMAL(15,6) NOT NULL,
    effective_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_currency_rate (from_currency, to_currency, effective_date)
);

-- ============================================
-- 17. AUDIT LOGS (Auto-generated)
-- ============================================
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    module VARCHAR(50) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    action ENUM('CREATE', 'UPDATE', 'DELETE', 'VIEW') NOT NULL,
    old_value TEXT,
    new_value TEXT,
    performed_by BIGINT,
    ip_address VARCHAR(50),
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 18. CERTIFICATE BLOCK LIST
-- ============================================
CREATE TABLE certificate_block_list (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    blocked_reason VARCHAR(500),
    pending_amount DECIMAL(12,2) NOT NULL,
    blocked_by BIGINT,
    blocked_date DATE DEFAULT (CURRENT_DATE),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE fee_structure_components (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fee_structure_id BIGINT NOT NULL,
    fee_type_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (fee_structure_id) REFERENCES fee_structures(id) ON DELETE CASCADE,
    FOREIGN KEY (fee_type_id) REFERENCES fee_types(id)
);

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================
CREATE INDEX idx_fee_allocations_user ON student_fee_allocations(user_id);
CREATE INDEX idx_payments_allocation ON student_fee_payments(student_fee_allocation_id);
CREATE INDEX idx_installments_allocation ON student_installment_plans(student_fee_allocation_id);
CREATE INDEX idx_audit_module_entity ON audit_logs(module, entity_id);
CREATE INDEX idx_refunds_status ON fee_refunds(refund_status);
CREATE INDEX idx_payments_status ON student_fee_payments(payment_status);