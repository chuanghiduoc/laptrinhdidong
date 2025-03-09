import { NextResponse } from "next/server";
import { db } from "@/lib/firebaseAdmin";

const studentsCollection = db.collection("students");

export async function GET() {
  try {
    const snapshot = await studentsCollection.get();
    const students = snapshot.docs.map((doc) => ({ id: doc.id, ...doc.data() }));
    return NextResponse.json(students);
  } catch (error) {
    return NextResponse.json({ error: "Failed to fetch students" }, { status: 500 });
  }
}

export async function POST(req: Request) {
  try {
    const body = await req.json();
    const docRef = await studentsCollection.add(body);
    return NextResponse.json({ id: docRef.id, ...body });
  } catch (error) {
    return NextResponse.json({ error: "Failed to add student" }, { status: 500 });
  }
}

export async function PUT(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "Missing ID" }, { status: 400 });

    const body = await req.json();
    await studentsCollection.doc(id).update(body);
    return NextResponse.json({ id, ...body });
  } catch (error) {
    return NextResponse.json({ error: "Failed to update student" }, { status: 500 });
  }
}

export async function DELETE(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "Missing ID" }, { status: 400 });

    await studentsCollection.doc(id).delete();
    return NextResponse.json({ success: true });
  } catch (error) {
    return NextResponse.json({ error: "Failed to delete student" }, { status: 500 });
  }
}
