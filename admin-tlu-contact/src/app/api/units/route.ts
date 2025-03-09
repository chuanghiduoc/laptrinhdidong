import { NextResponse } from "next/server";
import { db } from "@/lib/firebaseAdmin";

// Lấy danh sách tất cả Units
export async function GET() {
  try {
    console.log("Fetching units...");
    
    const snapshot = await db.collection("units").get();
    const data = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));

    console.log("Data fetched:", data);
    
    return NextResponse.json(data);
  } catch (error) {
    console.error("Error fetching units:", error);
    return NextResponse.json({ error: "Failed to fetch units" }, { status: 500 });
  }
}

// Thêm một Unit mới
export async function POST(req: Request) {
  try {
    const body = await req.json();
    const docRef = await db.collection("units").add(body);
    return NextResponse.json({ id: docRef.id, ...body });
  } catch (error) {
    console.error("Error adding unit:", error);
    return NextResponse.json({ error: "Failed to add unit" }, { status: 500 });
  }
}

// Cập nhật một Unit (PUT /api/units?id=...)
export async function PUT(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "Missing ID" }, { status: 400 });

    const body = await req.json();
    await db.collection("units").doc(id).update(body);
    return NextResponse.json({ id, ...body });
  } catch (error) {
    console.error("Error updating unit:", error);
    return NextResponse.json({ error: "Failed to update unit" }, { status: 500 });
  }
}

// Xóa một Unit (DELETE /api/units?id=...)
export async function DELETE(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "Missing ID" }, { status: 400 });

    await db.collection("units").doc(id).delete();
    return NextResponse.json({ success: true });
  } catch (error) {
    console.error("Error deleting unit:", error);
    return NextResponse.json({ error: "Failed to delete unit" }, { status: 500 });
  }
}
