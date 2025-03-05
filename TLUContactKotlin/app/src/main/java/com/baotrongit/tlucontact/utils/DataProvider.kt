package com.baotrongit.tlucontact.utils

import com.baotrongit.tlucontact.data.model.TLUUnit
import com.baotrongit.tlucontact.data.model.UnitType
import com.baotrongit.tlucontact.data.model.Staff
import com.baotrongit.tlucontact.data.model.Student

object DataProvider {

    fun getUnits(): List<TLUUnit> {
        return listOf(
            // Khoa
            TLUUnit(
                id = "faculty_1",
                name = "Khoa Công nghệ thông tin",
                code = "CNTT",
                type = UnitType.FACULTY,
                email = "cntt@tlu.edu.vn",
                phone = "024.3852.4529",
                address = "Nhà C1, Đại học Thủy lợi",
                website = "cse.tlu.edu.vn",
                description = "Khoa Công nghệ thông tin đào tạo các chuyên ngành về Công nghệ thông tin, Hệ thống thông tin, Kỹ thuật phần mềm...",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),
            TLUUnit(
                id = "faculty_2",
                name = "Khoa Kinh tế và Quản lý",
                code = "KT&QL",
                type = UnitType.FACULTY,
                email = "kinhte@tlu.edu.vn",
                phone = "024.3852.4628",
                address = "Nhà A1, Đại học Thủy lợi",
                website = "fem.tlu.edu.vn",
                description = "Khoa Kinh tế và Quản lý đào tạo các chuyên ngành về Kinh tế, Quản trị kinh doanh, Kế toán...",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),
            TLUUnit(
                id = "faculty_3",
                name = "Khoa Kỹ thuật xây dựng",
                code = "KTXD",
                type = UnitType.FACULTY,
                email = "kythuat@tlu.edu.vn",
                phone = "024.3852.4530",
                address = "Nhà A5, Đại học Thủy lợi",
                website = "ce.tlu.edu.vn",
                description = "Khoa Kỹ thuật xây dựng đào tạo các chuyên ngành về Xây dựng dân dụng và công nghiệp, Kỹ thuật cơ sở hạ tầng...",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),

            // Phòng
            TLUUnit(
                id = "department_1",
                name = "Phòng Đào tạo",
                code = "PDT",
                type = UnitType.DEPARTMENT,
                email = "daotao@tlu.edu.vn",
                phone = "024.3852.4529",
                address = "Nhà A1, Đại học Thủy lợi",
                description = "Phòng Đào tạo chịu trách nhiệm quản lý các hoạt động đào tạo của trường.",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),
            TLUUnit(
                id = "department_2",
                name = "Phòng Công tác sinh viên",
                code = "CTSV",
                type = UnitType.DEPARTMENT,
                email = "ctsv@tlu.edu.vn",
                phone = "024.3852.4531",
                address = "Nhà A1, Đại học Thủy lợi",
                description = "Phòng Công tác sinh viên chịu trách nhiệm quản lý các hoạt động của sinh viên.",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),
            TLUUnit(
                id = "department_3",
                name = "Phòng Tài chính kế toán",
                code = "TCKT",
                type = UnitType.DEPARTMENT,
                email = "tckt@tlu.edu.vn",
                phone = "024.3852.4532",
                address = "Nhà A1, Đại học Thủy lợi",
                description = "Phòng Tài chính kế toán chịu trách nhiệm quản lý tài chính của trường.",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),

            // Ban
            TLUUnit(
                id = "office_1",
                name = "Ban Quản lý đào tạo",
                code = "QLĐT",
                type = UnitType.OFFICE,
                email = "qldt@tlu.edu.vn",
                phone = "024.3852.4533",
                address = "Nhà A2, Đại học Thủy lợi",
                description = "Ban Quản lý đào tạo chịu trách nhiệm quản lý các chương trình đào tạo.",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),
            TLUUnit(
                id = "office_2",
                name = "Ban Khoa học công nghệ",
                code = "KHCN",
                type = UnitType.OFFICE,
                email = "khcn@tlu.edu.vn",
                phone = "024.3852.4534",
                address = "Nhà A3, Đại học Thủy lợi",
                description = "Ban Khoa học công nghệ chịu trách nhiệm quản lý các hoạt động nghiên cứu khoa học và chuyển giao công nghệ.",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),

            // Trung tâm
            TLUUnit(
                id = "center_1",
                name = "Trung tâm Thông tin Thư viện",
                code = "TTTV",
                type = UnitType.CENTER,
                email = "thuvien@tlu.edu.vn",
                phone = "024.3852.4535",
                address = "Nhà C5, Đại học Thủy lợi",
                website = "lib.tlu.edu.vn",
                description = "Trung tâm Thông tin Thư viện cung cấp tài liệu học tập và nghiên cứu cho sinh viên và giảng viên.",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),
            TLUUnit(
                id = "center_2",
                name = "Trung tâm Hợp tác quốc tế",
                code = "HTQT",
                type = UnitType.CENTER,
                email = "htqt@tlu.edu.vn",
                phone = "024.3852.4536",
                address = "Nhà A4, Đại học Thủy lợi",
                website = "iro.tlu.edu.vn",
                description = "Trung tâm Hợp tác quốc tế quản lý các hoạt động hợp tác với các đối tác quốc tế.",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),
            TLUUnit(
                id = "center_3",
                name = "Trung tâm Đào tạo quốc tế",
                code = "ĐTQT",
                type = UnitType.CENTER,
                email = "dtqt@tlu.edu.vn",
                phone = "024.3852.4537",
                address = "Nhà A4, Đại học Thủy lợi",
                website = "cie.tlu.edu.vn",
                description = "Trung tâm Đào tạo quốc tế quản lý các chương trình đào tạo hợp tác với các trường đại học nước ngoài.",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),

            // Khác
            TLUUnit(
                id = "other_1",
                name = "Viện Kinh tế Tài nguyên và Môi trường",
                code = "KTTM",
                type = UnitType.OTHER,
                email = "kttm@tlu.edu.vn",
                phone = "024.3852.4538",
                address = "Nhà B1, Đại học Thủy lợi",
                website = "iere.tlu.edu.vn",
                description = "Viện Kinh tế Tài nguyên và Môi trường nghiên cứu các vấn đề về kinh tế, tài nguyên và môi trường.",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            ),
            TLUUnit(
                id = "other_2",
                name = "Đoàn Thanh niên",
                code = "ĐTN",
                type = UnitType.OTHER,
                email = "doantn@tlu.edu.vn",
                phone = "024.3852.4539",
                address = "Nhà A2, Đại học Thủy lợi",
                website = "youth.tlu.edu.vn",
                description = "Đoàn Thanh niên tổ chức các hoạt động phong trào cho sinh viên.",
                logoUrl = "https://inkythuatso.com/uploads/images/2021/12/logo-dai-hoc-thuy-loi-inkythuatso-converted-01-23-08-44-48.jpg"
            )
        )
    }

    fun getStaff(): List<Staff> {
        return listOf(
            Staff(
                id = "staff_1",
                fullName = "Nguyễn Văn A",
                position = "Giảng viên",
                unitId = "faculty_1",
                email = "a@tlu.edu.vn",
                phone = "024.3852.4500",
                avatarUrl = "https://example.com/avatar_a.png"
            ),
            Staff(
                id = "staff_2",
                fullName = "Trần Thị B",
                position = "Cán bộ",
                unitId = "department_1",
                email = "b@tlu.edu.vn",
                phone = "024.3852.4501",
                avatarUrl = "https://example.com/avatar_b.png"
            ),
            Staff(
                id = "staff_3",
                fullName = "Lê Văn C",
                position = "Giảng viên",
                unitId = "faculty_2",
                email = "c@tlu.edu.vn",
                phone = "024.3852.4502",
                avatarUrl = "https://example.com/avatar_c.png"
            ),
            Staff(
                id = "staff_4",
                fullName = "Phạm Thị D",
                position = "Phó phòng",
                unitId = "office_1",
                email = "d@tlu.edu.vn",
                phone = "024.3852.4503",
                avatarUrl = "https://simgbb.com/avatar/Y7j81FmXjwmP.jpg"
            )
        )
    }

    fun getStudents(): List<Student> {
        return listOf(
            Student(
                id = "student_1",
                fullName = "Nguyễn Văn A",
                studentCode = "SV001",
                unitId = "faculty_1",
                email = "a@tlu.edu.vn",
                phone = "024.3852.4500",
                avatarUrl = "https://simgbb.com/avatar/Y7j81FmXjwmP.jpg"
            ),
            Student(
                id = "student_2",
                fullName = "Trần Thị B",
                studentCode = "SV002",
                unitId = "department_1",
                email = "b@tlu.edu.vn",
                phone = "024.3852.4501",
                avatarUrl = "https://simgbb.com/avatar/Y7j81FmXjwmP.jpg"
            ),
            Student(
                id = "student_3",
                fullName = "Lê Văn C",
                studentCode = "SV003",
                unitId = "faculty_2",
                email = "c@tlu.edu.vn",
                phone = "024.3852.4502",
                avatarUrl = "https://simgbb.com/avatar/Y7j81FmXjwmP.jpg"
            ),
            Student(
                id = "student_4",
                fullName = "Phạm Thị D",
                studentCode = "SV004",
                unitId = "office_1",
                email = "d@tlu.edu.vn",
                phone = "024.3852.4503",
                avatarUrl = "https://simgbb.com/avatar/Y7j81FmXjwmP.jpg"
            )
            // Thêm các sinh viên khác tại đây
        )
    }
}
