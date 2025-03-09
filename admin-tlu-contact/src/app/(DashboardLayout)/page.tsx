// import React from "react";
// import SalesProfit from "../components/dashboard/RevenueForecast";
// import NewCustomers from "../components/dashboard/NewCustomers";
// import TotalIncome from "../components/dashboard/TotalIncome";
// import ProductRevenue from "../components/dashboard/ProductRevenue";
// import DailyActivity from "../components/dashboard/DailyActivity";
// import BlogCards from "../components/dashboard/BlogCards";
// import Link from "next/link";

// const page = () => {
//   return (
//     <>
//       <div className="grid grid-cols-12 gap-30">
//         <div className="lg:col-span-8 col-span-12">
//           <SalesProfit />
//         </div>
//         <div className="lg:col-span-4 col-span-12">
//           <div className="grid grid-cols-12 h-full items-stretch">
//             <div className="col-span-12 mb-30">
//               <NewCustomers />
//             </div>
//             <div className="col-span-12">
//               <TotalIncome />
//             </div>
//           </div>
//         </div>
//         <div className="lg:col-span-8 col-span-12">
//           <ProductRevenue />
//         </div>
//         <div className="lg:col-span-4 col-span-12">
//           <DailyActivity />
//         </div>
//         <div className="col-span-12">
//           <BlogCards />
//         </div>
//         <div className="col-span-12 text-center">
//           <p className="text-base">
//             Design and Developed by{" "}
//             <Link
//               href="https://adminmart.com/"
//               target="_blank"
//               className="pl-1 text-primary underline decoration-primary"
//             >
//               adminmart.com
//             </Link>
//           </p>
//         </div>
//       </div>
//     </>
//   );
// };

// export default page;
"use client";

import { useEffect, useState } from "react";

interface Unit {
  id?: string;
  address: string;
  code: string;
  description: string;
  email: string;
  logoUrl: string;
  name: string;
  phone: string;
  type: string;
  website: string;
}

export default function UnitsPage() {
  const [units, setUnits] = useState<Unit[]>([]);
  const [currentUnit, setCurrentUnit] = useState<Unit | null>(null);
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const [formData, setFormData] = useState<Unit>({
    address: "",
    code: "",
    description: "",
    email: "",
    logoUrl: "",
    name: "",
    phone: "",
    type: "",
    website: "",
  });

  useEffect(() => {
    fetch("/api/units")
      .then((res) => res.json())
      .then((data) => setUnits(data))
      .catch((error) => console.error("Error fetching units:", error));
  }, []);

  const openFormModal = (unit?: Unit) => {
    setCurrentUnit(unit || null);
    setFormData(
      unit || {
        address: "",
        code: "",
        description: "",
        email: "",
        logoUrl: "",
        name: "",
        phone: "",
        type: "",
        website: "",
      }
    );
    setIsFormModalOpen(true);
  };

  const openDeleteModal = (unit: Unit) => {
    setCurrentUnit(unit);
    setIsDeleteModalOpen(true);
  };

  const handleSave = async () => {
    setLoading(true);
    try {
      const method = currentUnit ? "PUT" : "POST";
      const url = currentUnit ? `/api/units?id=${currentUnit.id}` : "/api/units";

      const res = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      if (res.ok) {
        const data = await res.json();
        setUnits((prev) =>
          currentUnit 
            ? prev.map((u) => (u.id === data.id ? data : u)) 
            : [...prev, data]
        );
        setIsFormModalOpen(false);
      } else {
        console.error("Error saving unit:", await res.text());
      }
    } catch (error) {
      console.error("Error saving unit:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!currentUnit) return;
    
    setLoading(true);
    try {
      const res = await fetch(`/api/units?id=${currentUnit.id}`, { 
        method: "DELETE" 
      });

      if (res.ok) {
        setUnits((prev) => prev.filter((u) => u.id !== currentUnit.id));
        setIsDeleteModalOpen(false);
      } else {
        console.error("Error deleting unit:", await res.text());
      }
    } catch (error) {
      console.error("Error deleting unit:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Quản lý Đơn vị</h1>
        <button
          onClick={() => openFormModal()}
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
          Thêm Đơn vị
        </button>
      </div>

      {/* Units Grid */}
      {units.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {units.map((unit) => (
            <div 
              key={unit.id} 
              className="bg-white rounded-lg shadow-md overflow-hidden border border-gray-200 hover:shadow-lg transition-shadow duration-300"
            >
              <div className="p-4 flex flex-col h-full">
                <div className="flex justify-center mb-4">
                  {unit.logoUrl ? (
                    <img 
                      src={unit.logoUrl} 
                      alt={unit.name} 
                      className="h-24 w-auto object-contain"
                      onError={(e) => {
                        (e.target as HTMLImageElement).src = 'https://via.placeholder.com/150?text=No+Logo';
                      }}
                    />
                  ) : (
                    <div className="h-24 w-24 bg-gray-100 flex items-center justify-center rounded-full">
                      <span className="text-2xl font-bold text-gray-400">
                        {unit.name.charAt(0)}
                      </span>
                    </div>
                  )}
                </div>
                
                <h2 className="text-xl font-semibold text-gray-800 mb-1">{unit.name}</h2>
                <div className="text-sm font-medium text-blue-600 mb-2">{unit.code}</div>
                
                <div className="space-y-2 text-sm text-gray-600 flex-grow">
                  {unit.address && (
                    <div className="flex items-start">
                      <svg className="h-5 w-5 mr-2 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                      </svg>
                      <span>{unit.address}</span>
                    </div>
                  )}
                  
                  {unit.email && (
                    <div className="flex items-start">
                      <svg className="h-5 w-5 mr-2 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                      </svg>
                      <span>{unit.email}</span>
                    </div>
                  )}
                  
                  {unit.phone && (
                    <div className="flex items-start">
                      <svg className="h-5 w-5 mr-2 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                      </svg>
                      <span>{unit.phone}</span>
                    </div>
                  )}
                  
                  {unit.type && (
                    <div className="flex items-start">
                      <svg className="h-5 w-5 mr-2 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                      </svg>
                      <span>{unit.type}</span>
                    </div>
                  )}
                </div>
                
                <div className="mt-4 pt-4 border-t border-gray-100 flex justify-between">
                  <button
                    onClick={() => openFormModal(unit)}
                    className="text-blue-600 hover:text-blue-800 font-medium text-sm flex items-center"
                  >
                    <svg className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                    Sửa
                  </button>
                  <button
                    onClick={() => openDeleteModal(unit)}
                    className="text-red-600 hover:text-red-800 font-medium text-sm flex items-center"
                  >
                    <svg className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                    Xóa
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow p-8 text-center">
          <svg 
            className="h-16 w-16 text-gray-300 mx-auto mb-4" 
            fill="none" 
            viewBox="0 0 24 24" 
            stroke="currentColor"
          >
            <path 
              strokeLinecap="round" 
              strokeLinejoin="round" 
              strokeWidth={1} 
              d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" 
            />
          </svg>
          <p className="text-gray-500 mb-4">Chưa có đơn vị nào được thêm</p>
          <button
            onClick={() => openFormModal()}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md transition-colors duration-200 inline-flex items-center"
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
            Thêm Đơn vị đầu tiên
          </button>
        </div>
      )}

      {/* Form Modal */}
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
                      {currentUnit ? "Chỉnh sửa đơn vị" : "Thêm đơn vị mới"}
                    </h3>
                    <div className="space-y-4">
                      <div>
                        <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                          Tên đơn vị <span className="text-red-500">*</span>
                        </label>
                        <input
                          type="text"
                          id="name"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Tên đơn vị"
                          value={formData.name}
                          onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                        />
                      </div>
                      
                      <div>
                        <label htmlFor="code" className="block text-sm font-medium text-gray-700">
                          Mã đơn vị <span className="text-red-500">*</span>
                        </label>
                        <input
                          type="text"
                          id="code"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Mã đơn vị"
                          value={formData.code}
                          onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                        />
                      </div>
                      
                      <div>
                        <label htmlFor="address" className="block text-sm font-medium text-gray-700">
                          Địa chỉ
                        </label>
                        <input
                          type="text"
                          id="address"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Địa chỉ"
                          value={formData.address}
                          onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                        />
                      </div>
                      
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                          <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                            Email
                          </label>
                          <input
                            type="email"
                            id="email"
                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                            placeholder="Email"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                          />
                        </div>
                        
                        <div>
                          <label htmlFor="phone" className="block text-sm font-medium text-gray-700">
                            Số điện thoại
                          </label>
                          <input
                            type="tel"
                            id="phone"
                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                            placeholder="Số điện thoại"
                            value={formData.phone}
                            onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                          />
                        </div>
                      </div>
                      
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                          <label htmlFor="website" className="block text-sm font-medium text-gray-700">
                            Website
                          </label>
                          <input
                            type="url"
                            id="website"
                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                            placeholder="Website"
                            value={formData.website}
                            onChange={(e) => setFormData({ ...formData, website: e.target.value })}
                          />
                        </div>
                        
                        <div>
                          <label htmlFor="type" className="block text-sm font-medium text-gray-700">
                            Loại đơn vị
                          </label>
                          <input
                            type="text"
                            id="type"
                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                            placeholder="Loại đơn vị"
                            value={formData.type}
                            onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                          />
                        </div>
                      </div>
                      
                      <div>
                        <label htmlFor="logoUrl" className="block text-sm font-medium text-gray-700">
                          URL Logo
                        </label>
                        <input
                          type="url"
                          id="logoUrl"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="URL Logo"
                          value={formData.logoUrl}
                          onChange={(e) => setFormData({ ...formData, logoUrl: e.target.value })}
                        />
                      </div>
                      
                      <div>
                        <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                          Mô tả
                        </label>
                        <textarea
                          id="description"
                          rows={3}
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="Mô tả"
                          value={formData.description}
                          onChange={(e) => setFormData({ ...formData, description: e.target.value })}
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
                    currentUnit ? "Cập nhật" : "Thêm mới"
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
                        Bạn có chắc chắn muốn xóa đơn vị <span className="font-medium text-gray-800">{currentUnit?.name}</span>? Hành động này không thể hoàn tác.
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
            </div>          </div>
        </div>
      )}
    </div>
  );
}