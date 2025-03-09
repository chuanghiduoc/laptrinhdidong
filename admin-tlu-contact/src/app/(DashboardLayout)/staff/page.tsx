"use client";

import { useState, useEffect } from "react";

interface Staff {
  id?: string;
  fullName: string;
  staffId: string;
  email: string;
  phone: string;
  position: string;
  avatarUrl: string;
  unitId: string;
}

export default function StaffPage() {
  const [staff, setStaff] = useState<Staff[]>([]);
  const [newStaff, setNewStaff] = useState<Staff>({
    fullName: "",
    staffId: "",
    email: "",
    phone: "",
    position: "",
    avatarUrl: "",
    unitId: "",
  });
  const [editingStaff, setEditingStaff] = useState<Staff | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [staffToDelete, setStaffToDelete] = useState<string | null>(null);

  useEffect(() => {
    fetch("/api/staff")
      .then((res) => res.json())
      .then((data) => setStaff(data))
      .catch((error) => console.error("Error fetching staff data:", error));
  }, []);

  const openAddModal = () => {
    setEditingStaff(null);
    setNewStaff({
      fullName: "",
      staffId: "",
      email: "",
      phone: "",
      position: "",
      avatarUrl: "",
      unitId: "",
    });
    setIsModalOpen(true);
  };

  const openEditModal = (staffMember: Staff) => {
    setEditingStaff(staffMember);
    setNewStaff({ ...staffMember });
    setIsModalOpen(true);
  };

  const openDeleteModal = (id: string) => {
    setStaffToDelete(id);
    setIsDeleteModalOpen(true);
  };

  const handleSave = async () => {
    const method = editingStaff ? "PUT" : "POST";
    const url = editingStaff ? `/api/staff?id=${editingStaff.id}` : "/api/staff";

    try {
      const res = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newStaff),
      });

      if (res.ok) {
        const data = await res.json();
        setStaff((prev) =>
          editingStaff
            ? prev.map((s) => (s.id === data.id ? data : s))
            : [...prev, data]
        );
        setIsModalOpen(false);
        setNewStaff({
          fullName: "",
          staffId: "",
          email: "",
          phone: "",
          position: "",
          avatarUrl: "",
          unitId: "",
        });
        setEditingStaff(null);
      } else {
        console.error("Error saving staff:", await res.text());
      }
    } catch (error) {
      console.error("Error saving staff:", error);
    }
  };

  const handleDelete = async () => {
    if (!staffToDelete) return;
    
    try {
      const res = await fetch(`/api/staff?id=${staffToDelete}`, {
        method: "DELETE",
      });
      
      if (res.ok) {
        setStaff((prev) => prev.filter((s) => s.id !== staffToDelete));
        setIsDeleteModalOpen(false);
        setStaffToDelete(null);
      } else {
        console.error("Error deleting staff:", await res.text());
      }
    } catch (error) {
      console.error("Error deleting staff:", error);
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Quản lý Nhân viên</h1>
        <button
          onClick={openAddModal}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md transition-colors duration-200 flex items-center"
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
          Thêm Nhân viên
        </button>
      </div>

      {/* Staff Table */}
      <div className="overflow-x-auto bg-white shadow-md rounded-lg">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Nhân viên
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Mã NV
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Email
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Số điện thoại
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Chức vụ
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {staff.length > 0 ? (
              staff.map((s) => (
                <tr key={s.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="h-10 w-10 flex-shrink-0">
                        {s.avatarUrl ? (
                          <img
                            className="h-10 w-10 rounded-full object-cover"
                            src={s.avatarUrl}
                            alt={s.fullName}
                          />
                        ) : (
                          <div className="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center text-gray-500">
                            {s.fullName.charAt(0)}
                          </div>
                        )}
                      </div>
                      <div className="ml-4">
                        <div className="text-sm font-medium text-gray-900">
                          {s.fullName}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {s.staffId}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {s.email}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {s.phone}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {s.position}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button
                      onClick={() => openEditModal(s)}
                      className="text-indigo-600 hover:text-indigo-900 mr-4"
                    >
                      Sửa
                    </button>
                    <button
                      onClick={() => openDeleteModal(s.id!)}
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
                  className="px-6 py-4 text-center text-sm text-gray-500"
                >
                  Không có dữ liệu nhân viên
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Add/Edit Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-10 overflow-y-auto">
          <div className="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
            <div
              className="fixed inset-0 transition-opacity"
              aria-hidden="true"
              onClick={() => setIsModalOpen(false)}
            >
              <div className="absolute inset-0 bg-gray-500 opacity-75"></div>
            </div>

            <span
              className="hidden sm:inline-block sm:align-middle sm:h-screen"
              aria-hidden="true"
            >
              &#8203;
            </span>

            <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
              <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                <div className="sm:flex sm:items-start">
                  <div className="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left w-full">
                    <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                      {editingStaff ? "Chỉnh sửa" : "Thêm"} Nhân viên
                    </h3>
                    <div className="space-y-4">
                      <div>
                        <label
                          htmlFor="fullName"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Họ và tên
                        </label>
                        <input
                          type="text"
                          id="fullName"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Họ và tên"
                          value={newStaff.fullName}
                          onChange={(e) =>
                            setNewStaff({
                              ...newStaff,
                              fullName: e.target.value,
                            })
                          }
                        />
                      </div>
                      <div>
                        <label
                          htmlFor="staffId"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Mã nhân viên
                        </label>
                        <input
                          type="text"
                          id="staffId"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Mã nhân viên"
                          value={newStaff.staffId}
                          onChange={(e) =>
                            setNewStaff({
                              ...newStaff,
                              staffId: e.target.value,
                            })
                          }
                        />
                      </div>
                      <div>
                        <label
                          htmlFor="email"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Email
                        </label>
                        <input
                          type="email"
                          id="email"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Email"
                          value={newStaff.email}
                          onChange={(e) =>
                            setNewStaff({
                              ...newStaff,
                              email: e.target.value,
                            })
                          }
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
                          value={newStaff.phone}
                          onChange={(e) =>
                            setNewStaff({
                              ...newStaff,
                              phone: e.target.value,
                            })
                          }
                        />
                      </div>
                      <div>
                        <label
                          htmlFor="position"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Chức vụ
                        </label>
                        <input
                          type="text"
                          id="position"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Chức vụ"
                          value={newStaff.position}
                          onChange={(e) =>
                            setNewStaff({
                              ...newStaff,
                              position: e.target.value,
                            })
                          }
                        />
                      </div>
                      <div>
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
                          value={newStaff.avatarUrl}
                          onChange={(e) =>
                            setNewStaff({
                              ...newStaff,
                              avatarUrl: e.target.value,
                            })
                          }
                        />
                      </div>
                      <div>
                        <label
                          htmlFor="unitId"
                          className="block text-sm font-medium text-gray-700"
                        >
                          ID Đơn vị
                        </label>
                        <input
                          type="text"
                          id="unitId"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="ID Đơn vị"
                          value={newStaff.unitId}
                          onChange={(e) =>
                            setNewStaff({
                              ...newStaff,
                              unitId: e.target.value,
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
                  className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:ml-3 sm:w-auto sm:text-sm"
                  onClick={handleSave}
                >
                  {editingStaff ? "Cập nhật" : "Thêm"}
                </button>
                <button
                  type="button"
                  className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
                  onClick={() => setIsModalOpen(false)}
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
              onClick={() => setIsDeleteModalOpen(false)}
            >
              <div className="absolute inset-0 bg-gray-500 opacity-75"></div>
            </div>

            <span
              className="hidden sm:inline-block sm:align-middle sm:h-screen"
              aria-hidden="true"
            >
              &#8203;
            </span>

            <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
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
                        Bạn có chắc chắn muốn xóa nhân viên này? Hành động này
                        không thể hoàn tác.
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                <button
                  type="button"
                  className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-red-600 text-base font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:ml-3 sm:w-auto sm:text-sm"
                  onClick={handleDelete}
                >
                  Xóa
                </button>
                <button
                  type="button"
                  className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
                  onClick={() => setIsDeleteModalOpen(false)}
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