"use client";

import { useState, useEffect } from "react";

interface Student {
  id?: string;
  fullName: string;
  studentId: string;
  email: string;
  phone: string;
  classId: string;
  address: string;
  avatarUrl: string;
  unitId: string;
}

export default function StudentsPage() {
  const [students, setStudents] = useState<Student[]>([]);
  const [newStudent, setNewStudent] = useState<Student>({
    fullName: "",
    studentId: "",
    email: "",
    phone: "",
    classId: "",
    address: "",
    avatarUrl: "",
    unitId: "",
  });
  const [editingStudent, setEditingStudent] = useState<Student | null>(null);
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [studentToDelete, setStudentToDelete] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    setLoading(true);
    fetch("/api/students")
      .then((res) => res.json())
      .then((data) => {
        setStudents(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching students:", error);
        setLoading(false);
      });
  }, []);

  // Sửa lỗi: Kiểm tra giá trị undefined trước khi gọi toLowerCase()
  const filteredStudents = students.filter((student) => {
    const searchLower = searchTerm.toLowerCase();
    return (
      (student.fullName || "").toLowerCase().includes(searchLower) ||
      (student.studentId || "").toLowerCase().includes(searchLower) ||
      (student.email || "").toLowerCase().includes(searchLower) ||
      (student.classId || "").toLowerCase().includes(searchLower)
    );
  });

  const openAddModal = () => {
    setEditingStudent(null);
    setNewStudent({
      fullName: "",
      studentId: "",
      email: "",
      phone: "",
      classId: "",
      address: "",
      avatarUrl: "",
      unitId: "",
    });
    setIsFormModalOpen(true);
  };

  const openEditModal = (student: Student) => {
    setEditingStudent(student);
    setNewStudent({ ...student });
    setIsFormModalOpen(true);
  };

  const openDeleteModal = (id: string) => {
    setStudentToDelete(id);
    setIsDeleteModalOpen(true);
  };

  const handleSave = async () => {
    setLoading(true);
    try {
      const method = editingStudent ? "PUT" : "POST";
      const url = editingStudent
        ? `/api/students?id=${editingStudent.id}`
        : "/api/students";

      const res = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newStudent),
      });

      if (res.ok) {
        const data = await res.json();
        setStudents((prev) =>
          editingStudent
            ? prev.map((s) => (s.id === data.id ? data : s))
            : [...prev, data]
        );
        setIsFormModalOpen(false);
        setNewStudent({
          fullName: "",
          studentId: "",
          email: "",
          phone: "",
          classId: "",
          address: "",
          avatarUrl: "",
          unitId: "",
        });
        setEditingStudent(null);
      } else {
        console.error("Error saving student:", await res.text());
      }
    } catch (error) {
      console.error("Error saving student:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!studentToDelete) return;
    
    setLoading(true);
    try {
      const res = await fetch(`/api/students?id=${studentToDelete}`, {
        method: "DELETE",
      });
      
      if (res.ok) {
        setStudents((prev) => prev.filter((s) => s.id !== studentToDelete));
        setIsDeleteModalOpen(false);
        setStudentToDelete(null);
      } else {
        console.error("Error deleting student:", await res.text());
      }
    } catch (error) {
      console.error("Error deleting student:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
        <h1 className="text-2xl font-bold text-gray-800">Quản lý Sinh viên</h1>
        
        <div className="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
          <div className="relative flex-grow">
            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
              <svg className="w-4 h-4 text-gray-500" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"/>
              </svg>
            </div>
            <input
              type="search"
              className="block w-full p-2 pl-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Tìm kiếm sinh viên..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          
          <button
            onClick={openAddModal}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md transition-colors duration-200 flex items-center justify-center"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5 mr-2"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fillRule="evenodd"
                d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z"
                clipRule="evenodd"
              />
            </svg>
            Thêm Sinh viên
          </button>
        </div>
      </div>

      {/* Loading Indicator */}
      {loading && !isFormModalOpen && !isDeleteModalOpen && (
        <div className="flex justify-center items-center py-8">
          <svg className="animate-spin h-8 w-8 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </div>
      )}

      {/* Students Table */}
      {!loading && (
        <div className="overflow-x-auto bg-white shadow-md rounded-lg">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Sinh viên
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Mã SV
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Lớp
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Liên hệ
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Địa chỉ
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredStudents.length > 0 ? (
                filteredStudents.map((student) => (
                  <tr key={student.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <div className="h-10 w-10 flex-shrink-0">
                          {student.avatarUrl ? (
                            <img
                              className="h-10 w-10 rounded-full object-cover"
                              src={student.avatarUrl}
                              alt={student.fullName}
                              onError={(e) => {
                                (e.target as HTMLImageElement).src = 'https://via.placeholder.com/150?text=SV';
                              }}
                            />
                          ) : (
                            <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-medium">
                              {student.fullName ? student.fullName.charAt(0) : "?"}
                            </div>
                          )}
                        </div>
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900">
                            {student.fullName}
                          </div>
                          <div className="text-sm text-gray-500">
                            {student.email}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {student.studentId}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                        {student.classId}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {student.phone}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 max-w-xs truncate">
                      {student.address}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <button
                        onClick={() => openEditModal(student)}
                        className="text-indigo-600 hover:text-indigo-900 mr-4"
                      >
                        Sửa
                      </button>
                      <button
                        onClick={() => openDeleteModal(student.id!)}
                        className="text-red-600 hover:text-red-900"
                      >
                        Xóa
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan={6}
                    className="px-6 py-10 text-center text-sm text-gray-500"
                  >
                    {searchTerm 
                      ? "Không tìm thấy sinh viên nào phù hợp với từ khóa tìm kiếm" 
                      : "Chưa có dữ liệu sinh viên"}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Add/Edit Modal */}
      {isFormModalOpen && (
        <div className="fixed inset-0 z-10 overflow-y-auto">
          <div className="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
            <div
              className="fixed inset-0 transition-opacity"
              aria-hidden="true"
              onClick={() => !loading && setIsFormModalOpen(false)}
            >
              <div className="absolute inset-0 bg-gray-500 opacity-75"></div>
            </div>

            <span
              className="hidden sm:inline-block sm:align-middle sm:h-screen"
              aria-hidden="true"
            >
              &#8203;
            </span>

            <div 
              className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                <div className="sm:flex sm:items-start">
                  <div className="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left w-full">
                    <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                      {editingStudent ? "Chỉnh sửa" : "Thêm"} Sinh viên
                    </h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="md:col-span-2">
                        <label
                          htmlFor="fullName"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Họ và tên <span className="text-red-500">*</span>
                        </label>
                        <input
                          type="text"
                          id="fullName"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Họ và tên"
                          value={newStudent.fullName}
                          onChange={(e) =>
                            setNewStudent({
                              ...newStudent,
                              fullName: e.target.value,
                            })
                          }
                          required
                        />
                      </div>
                      
                      <div>
                        <label
                          htmlFor="studentId"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Mã sinh viên <span className="text-red-500">*</span>
                        </label>
                        <input
                          type="text"
                          id="studentId"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Mã sinh viên"
                          value={newStudent.studentId}
                          onChange={(e) =>
                            setNewStudent({
                              ...newStudent,
                              studentId: e.target.value,
                            })
                          }
                          required
                        />
                      </div>
                      
                      <div>
                        <label
                          htmlFor="classId"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Lớp <span className="text-red-500">*</span>
                        </label>
                        <input
                          type="text"
                          id="classId"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Lớp"
                          value={newStudent.classId}
                          onChange={(e) =>
                            setNewStudent({
                              ...newStudent,
                              classId: e.target.value,
                            })
                          }
                          required
                        />
                      </div>
                      
                      <div className="md:col-span-2">
                        <label
                          htmlFor="email"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Email <span className="text-red-500">*</span>
                        </label>
                        <input
                          type="email"
                          id="email"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Email"
                          value={newStudent.email}
                          onChange={(e) =>
                            setNewStudent({
                              ...newStudent,
                              email: e.target.value,
                            })
                          }
                          required
                        />
                      </div>
                      
                      <div>
                        <label
                          htmlFor="phone"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Số điện thoại
                        </label>
                        <input
                          type="tel"
                          id="phone"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Số điện thoại"
                          value={newStudent.phone}
                          onChange={(e) =>
                            setNewStudent({
                              ...newStudent,
                              phone: e.target.value,
                            })
                          }
                        />
                      </div>
                      
                      <div>
                        <label
                          htmlFor="unitId"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Mã đơn vị
                        </label>
                        <input
                          type="text"
                          id="unitId"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Mã đơn vị"
                          value={newStudent.unitId}
                          onChange={(e) =>
                            setNewStudent({
                              ...newStudent,
                              unitId: e.target.value,
                            })
                          }
                        />
                      </div>
                      
                      <div className="md:col-span-2">
                        <label
                          htmlFor="address"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Địa chỉ
                        </label>
                        <input
                          type="text"
                          id="address"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Địa chỉ"
                          value={newStudent.address}
                          onChange={(e) =>
                            setNewStudent({
                              ...newStudent,
                              address: e.target.value,
                            })
                          }
                        />
                      </div>
                      
                      <div className="md:col-span-2">
                        <label
                          htmlFor="avatarUrl"
                          className="block text-sm font-medium text-gray-700"
                        >
                          URL Ảnh đại diện
                        </label>
                        <input
                          type="text"
                          id="avatarUrl"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="URL Ảnh đại diện"
                          value={newStudent.avatarUrl}
                          onChange={(e) =>
                            setNewStudent({
                              ...newStudent,
                              avatarUrl: e.target.value,
                            })
                          }
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                <button
                  type="button"
                  className={`w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 ${
                    loading 
                      ? "bg-blue-400 cursor-not-allowed" 
                      : "bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  } text-base font-medium text-white sm:ml-3 sm:w-auto sm:text-sm`}
                  onClick={handleSave}
                  disabled={loading}
                >
                  {loading ? (
                    <>
                      <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      Đang lưu...
                    </>
                  ) : (
                    editingStudent ? "Cập nhật" : "Thêm"
                  )}
                </button>
                <button
                  type="button"
                  className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
                  onClick={() => !loading && setIsFormModalOpen(false)}
                  disabled={loading}
                >
                  Hủy
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {isDeleteModalOpen && (
        <div className="fixed inset-0 z-10 overflow-y-auto">
          <div className="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
            <div
              className="fixed inset-0 transition-opacity"
              aria-hidden="true"
              onClick={() => !loading && setIsDeleteModalOpen(false)}
            >
              <div className="absolute inset-0 bg-gray-500 opacity-75"></div>
            </div>

            <span
              className="hidden sm:inline-block sm:align-middle sm:h-screen"
              aria-hidden="true"
            >
              &#8203;
            </span>

            <div 
              className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                <div className="sm:flex sm:items-start">
                  <div className="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10">
                    <svg
                      className="h-6 w-6 text-red-600"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      aria-hidden="true"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth="2"
                        d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                      />
                    </svg>
                  </div>
                  <div className="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left">
                    <h3
                      className="text-lg leading-6 font-medium text-gray-900"
                      id="modal-title"
                    >
                      Xác nhận xóa
                    </h3>
                    <div className="mt-2">
                      <p className="text-sm text-gray-500">
                        Bạn có chắc chắn muốn xóa sinh viên này? Hành động này
                        không thể hoàn tác.
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                <button
                  type="button"
                  className={`w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 ${
                    loading 
                      ? "bg-red-400 cursor-not-allowed" 
                      : "bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                  } text-base font-medium text-white sm:ml-3 sm:w-auto sm:text-sm`}
                  onClick={handleDelete}
                  disabled={loading}
                >
                  {loading ? (
                    <>
                      <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      Đang xóa...
                    </>
                  ) : (
                    "Xóa"
                  )}
                </button>
                <button
                  type="button"
                  className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
                  onClick={() => !loading && setIsDeleteModalOpen(false)}
                  disabled={loading}
                >
                  Hủy
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
