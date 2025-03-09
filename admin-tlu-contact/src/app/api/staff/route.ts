import { NextResponse } from "next/server";
import { db } from "@/lib/firebaseAdmin";

const staffCollection = db.collection("staff");

export async function GET() {
  try {
    const snapshot = await staffCollection.get();
    const staff = snapshot.docs.map((doc) => ({ id: doc.id, ...doc.data() }));
    return NextResponse.json(staff);
  } catch (error) {
    return NextResponse.json({ error: "Failed to fetch staff" }, { status: 500 });
  }
}

export async function POST(req: Request) {
  try {
    const body = await req.json();
    const docRef = await staffCollection.add(body);
    return NextResponse.json({ id: docRef.id, ...body });
  } catch (error) {
    return NextResponse.json({ error: "Failed to add staff" }, { status: 500 });
  }
}

export async function PUT(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "Missing ID" }, { status: 400 });

    const body = await req.json();
    await staffCollection.doc(id).update(body);
    return NextResponse.json({ id, ...body });
  } catch (error) {
    return NextResponse.json({ error: "Failed to update staff" }, { status: 500 });
  }
}

export async function DELETE(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "Missing ID" }, { status: 400 });

    await staffCollection.doc(id).delete();
    return NextResponse.json({ success: true });
  } catch (error) {
    return NextResponse.json({ error: "Failed to delete staff" }, { status: 500 });
  }
}
