package rs2;

import rs2.graphics.RSDrawingArea;
import rs2.graphics.Rasterizer;
import rs2.resource.ModelProvider;

public final class Model extends Animable {

	public static void nullLoader() {
		aClass21Array1661 = null;
		aBooleanArray1663 = null;
		aBooleanArray1664 = null;
		anIntArray1665 = null;
		anIntArray1666 = null;
		anIntArray1667 = null;
		anIntArray1668 = null;
		anIntArray1669 = null;
		anIntArray1670 = null;
		anIntArray1671 = null;
		anIntArrayArray1672 = null;
		anIntArray1673 = null;
		anIntArrayArray1674 = null;
		anIntArray1675 = null;
		anIntArray1676 = null;
		anIntArray1677 = null;
		SINE = null;
		COSINE = null;
		modelIntArray3 = null;
		modelIntArray4 = null;
	}

	public static void method459(int i,
			ModelProvider onDemandFetcherParent) {
		aClass21Array1661 = new ModelHeader[i];
		aOnDemandFetcherParent_1662 = onDemandFetcherParent;
	}

	public static void method460(byte abyte0[], int j) {
		if (abyte0 == null) {
			ModelHeader class21 = aClass21Array1661[j] = new ModelHeader();
			class21.modelVerticeCount = 0;
			class21.modelTriangleCount = 0;
			class21.modelTextureTriangleCount = 0;
			return;
		}
		JagexBuffer stream = new JagexBuffer(abyte0);
		stream.offset = abyte0.length - 18;
		ModelHeader class21_1 = aClass21Array1661[j] = new ModelHeader();
		class21_1.modelData = abyte0;
		class21_1.modelVerticeCount = stream.getUnsignedShort();
		class21_1.modelTriangleCount = stream.getUnsignedShort();
		class21_1.modelTextureTriangleCount = stream.getUnsignedByte();
		int k = stream.getUnsignedByte();
		int l = stream.getUnsignedByte();
		int i1 = stream.getUnsignedByte();
		int j1 = stream.getUnsignedByte();
		int k1 = stream.getUnsignedByte();
		int l1 = stream.getUnsignedShort();
		int i2 = stream.getUnsignedShort();
		int j2 = stream.getUnsignedShort();
		int k2 = stream.getUnsignedShort();
		int l2 = 0;
		class21_1.vertexModOffset = l2;
		l2 += class21_1.modelVerticeCount;
		class21_1.triMeshLinkOffset = l2;
		l2 += class21_1.modelTriangleCount;
		class21_1.facePriorityBasePos = l2;
		if (l == 255)
			l2 += class21_1.modelTriangleCount;
		else
			class21_1.facePriorityBasePos = -l - 1;
		class21_1.tskinBasePos = l2;
		if (j1 == 1)
			l2 += class21_1.modelTriangleCount;
		else
			class21_1.tskinBasePos = -1;
		class21_1.drawTypeBasePos = l2;
		if (k == 1)
			l2 += class21_1.modelTriangleCount;
		else
			class21_1.drawTypeBasePos = -1;
		class21_1.vskinBasePos = l2;
		if (k1 == 1)
			l2 += class21_1.modelVerticeCount;
		else
			class21_1.vskinBasePos = -1;
		class21_1.alphaBasePos = l2;
		if (i1 == 1)
			l2 += class21_1.modelTriangleCount;
		else
			class21_1.alphaBasePos = -1;
		class21_1.triVPointOffset = l2;
		l2 += k2;
		class21_1.triColourOffset = l2;
		l2 += class21_1.modelTriangleCount * 2;
		class21_1.textureInfoBasePos = l2;
		l2 += class21_1.modelTextureTriangleCount * 6;
		class21_1.vertexXOffset = l2;
		l2 += l1;
		class21_1.vertexYOffset = l2;
		l2 += i2;
		class21_1.vertexZOffset = l2;
		l2 += j2;
	}

	public static void method461(int j) {
		aClass21Array1661[j] = null;
	}

	public static Model method462(int j) {
		if (aClass21Array1661 == null)
			return null;
		ModelHeader class21 = aClass21Array1661[j];
		if (class21 == null) {
			aOnDemandFetcherParent_1662.method548(j);
			return null;
		} else {
			return new Model(j);
		}
	}

	public static boolean method463(int i) {
		if (aClass21Array1661 == null)
			return false;
		ModelHeader class21 = aClass21Array1661[i];
		if (class21 == null) {
			aOnDemandFetcherParent_1662.method548(i);
			return false;
		} else {
			return true;
		}
	}

	private Model() {
		aBoolean1659 = false;
	}

	private Model(int i) {
		aBoolean1659 = false;
		ModelHeader class21 = aClass21Array1661[i];
		totalVertices = class21.modelVerticeCount;
		triangleCount = class21.modelTriangleCount;
		anInt1642 = class21.modelTextureTriangleCount;
		vertexX = new int[totalVertices];
		vertexY = new int[totalVertices];
		vertexZ = new int[totalVertices];
		triangleA = new int[triangleCount];
		triangleB = new int[triangleCount];
		triangleC = new int[triangleCount];
		anIntArray1643 = new int[anInt1642];
		anIntArray1644 = new int[anInt1642];
		anIntArray1645 = new int[anInt1642];
		if (class21.vskinBasePos >= 0)
			anIntArray1655 = new int[totalVertices];
		if (class21.drawTypeBasePos >= 0)
			triangleDrawType = new int[triangleCount];
		if (class21.facePriorityBasePos >= 0)
			priorities = new int[triangleCount];
		else
			anInt1641 = -class21.facePriorityBasePos - 1;
		if (class21.alphaBasePos >= 0)
			alpha = new int[triangleCount];
		if (class21.tskinBasePos >= 0)
			anIntArray1656 = new int[triangleCount];
		colors = new int[triangleCount];
		JagexBuffer stream = new JagexBuffer(class21.modelData);
		stream.offset = class21.vertexModOffset;
		JagexBuffer stream_1 = new JagexBuffer(class21.modelData);
		stream_1.offset = class21.vertexXOffset;
		JagexBuffer stream_2 = new JagexBuffer(class21.modelData);
		stream_2.offset = class21.vertexYOffset;
		JagexBuffer stream_3 = new JagexBuffer(class21.modelData);
		stream_3.offset = class21.vertexZOffset;
		JagexBuffer stream_4 = new JagexBuffer(class21.modelData);
		stream_4.offset = class21.vskinBasePos;
		int k = 0;
		int l = 0;
		int i1 = 0;
		for (int j1 = 0; j1 < totalVertices; j1++) {
			int k1 = stream.getUnsignedByte();
			int i2 = 0;
			if ((k1 & 1) != 0)
				i2 = stream_1.getUnsignedSmart();
			int k2 = 0;
			if ((k1 & 2) != 0)
				k2 = stream_2.getUnsignedSmart();
			int i3 = 0;
			if ((k1 & 4) != 0)
				i3 = stream_3.getUnsignedSmart();
			vertexX[j1] = k + i2;
			vertexY[j1] = l + k2;
			vertexZ[j1] = i1 + i3;
			k = vertexX[j1];
			l = vertexY[j1];
			i1 = vertexZ[j1];
			if (anIntArray1655 != null)
				anIntArray1655[j1] = stream_4.getUnsignedByte();
		}

		stream.offset = class21.triColourOffset;
		stream_1.offset = class21.drawTypeBasePos;
		stream_2.offset = class21.facePriorityBasePos;
		stream_3.offset = class21.alphaBasePos;
		stream_4.offset = class21.tskinBasePos;
		for (int color = 0; color < triangleCount; color++) {
			colors[color] = stream.getUnsignedShort();
			if (triangleDrawType != null)
				triangleDrawType[color] = stream_1.getUnsignedByte();
			if (priorities != null)
				priorities[color] = stream_2.getUnsignedByte();
			if (alpha != null)
				alpha[color] = stream_3.getUnsignedByte();
			if (anIntArray1656 != null)
				anIntArray1656[color] = stream_4.getUnsignedByte();
		}

		stream.offset = class21.triVPointOffset;
		stream_1.offset = class21.triMeshLinkOffset;
		int j2 = 0;
		int l2 = 0;
		int j3 = 0;
		int k3 = 0;
		for (int l3 = 0; l3 < triangleCount; l3++) {
			int i4 = stream_1.getUnsignedByte();
			if (i4 == 1) {
				j2 = stream.getUnsignedSmart() + k3;
				k3 = j2;
				l2 = stream.getUnsignedSmart() + k3;
				k3 = l2;
				j3 = stream.getUnsignedSmart() + k3;
				k3 = j3;
				triangleA[l3] = j2;
				triangleB[l3] = l2;
				triangleC[l3] = j3;
			}
			if (i4 == 2) {
				l2 = j3;
				j3 = stream.getUnsignedSmart() + k3;
				k3 = j3;
				triangleA[l3] = j2;
				triangleB[l3] = l2;
				triangleC[l3] = j3;
			}
			if (i4 == 3) {
				j2 = j3;
				j3 = stream.getUnsignedSmart() + k3;
				k3 = j3;
				triangleA[l3] = j2;
				triangleB[l3] = l2;
				triangleC[l3] = j3;
			}
			if (i4 == 4) {
				int k4 = j2;
				j2 = l2;
				l2 = k4;
				j3 = stream.getUnsignedSmart() + k3;
				k3 = j3;
				triangleA[l3] = j2;
				triangleB[l3] = l2;
				triangleC[l3] = j3;
			}
		}

		stream.offset = class21.textureInfoBasePos;
		for (int j4 = 0; j4 < anInt1642; j4++) {
			anIntArray1643[j4] = stream.getUnsignedShort();
			anIntArray1644[j4] = stream.getUnsignedShort();
			anIntArray1645[j4] = stream.getUnsignedShort();
		}

	}

	public Model(int i, Model aclass30_sub2_sub4_sub6s[]) {
		aBoolean1659 = false;
		boolean flag = false;
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		totalVertices = 0;
		triangleCount = 0;
		anInt1642 = 0;
		anInt1641 = -1;
		for (int k = 0; k < i; k++) {
			Model model = aclass30_sub2_sub4_sub6s[k];
			if (model != null) {
				totalVertices += model.totalVertices;
				triangleCount += model.triangleCount;
				anInt1642 += model.anInt1642;
				flag |= model.triangleDrawType != null;
				if (model.priorities != null) {
					flag1 = true;
				} else {
					if (anInt1641 == -1)
						anInt1641 = model.anInt1641;
					if (anInt1641 != model.anInt1641)
						flag1 = true;
				}
				flag2 |= model.alpha != null;
				flag3 |= model.anIntArray1656 != null;
			}
		}

		vertexX = new int[totalVertices];
		vertexY = new int[totalVertices];
		vertexZ = new int[totalVertices];
		anIntArray1655 = new int[totalVertices];
		triangleA = new int[triangleCount];
		triangleB = new int[triangleCount];
		triangleC = new int[triangleCount];
		anIntArray1643 = new int[anInt1642];
		anIntArray1644 = new int[anInt1642];
		anIntArray1645 = new int[anInt1642];
		if (flag)
			triangleDrawType = new int[triangleCount];
		if (flag1)
			priorities = new int[triangleCount];
		if (flag2)
			alpha = new int[triangleCount];
		if (flag3)
			anIntArray1656 = new int[triangleCount];
		colors = new int[triangleCount];
		totalVertices = 0;
		triangleCount = 0;
		anInt1642 = 0;
		int l = 0;
		for (int i1 = 0; i1 < i; i1++) {
			Model model_1 = aclass30_sub2_sub4_sub6s[i1];
			if (model_1 != null) {
				for (int j1 = 0; j1 < model_1.triangleCount; j1++) {
					if (flag)
						if (model_1.triangleDrawType == null) {
							triangleDrawType[triangleCount] = 0;
						} else {
							int k1 = model_1.triangleDrawType[j1];
							if ((k1 & 2) == 2)
								k1 += l << 2;
							triangleDrawType[triangleCount] = k1;
						}
					if (flag1)
						if (model_1.priorities == null)
							priorities[triangleCount] = model_1.anInt1641;
						else
							priorities[triangleCount] = model_1.priorities[j1];
					if (flag2)
						if (model_1.alpha == null)
							alpha[triangleCount] = 0;
						else
							alpha[triangleCount] = model_1.alpha[j1];
					if (flag3 && model_1.anIntArray1656 != null)
						anIntArray1656[triangleCount] = model_1.anIntArray1656[j1];
					colors[triangleCount] = model_1.colors[j1];
					triangleA[triangleCount] = method465(model_1,
							model_1.triangleA[j1]);
					triangleB[triangleCount] = method465(model_1,
							model_1.triangleB[j1]);
					triangleC[triangleCount] = method465(model_1,
							model_1.triangleC[j1]);
					triangleCount++;
				}

				for (int l1 = 0; l1 < model_1.anInt1642; l1++) {
					anIntArray1643[anInt1642] = method465(model_1,
							model_1.anIntArray1643[l1]);
					anIntArray1644[anInt1642] = method465(model_1,
							model_1.anIntArray1644[l1]);
					anIntArray1645[anInt1642] = method465(model_1,
							model_1.anIntArray1645[l1]);
					anInt1642++;
				}

				l += model_1.anInt1642;
			}
		}

	}

	public Model(Model aclass30_sub2_sub4_sub6s[]) {
		int i = 2;// was parameter
		aBoolean1659 = false;
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean flag4 = false;
		totalVertices = 0;
		triangleCount = 0;
		anInt1642 = 0;
		anInt1641 = -1;
		for (int k = 0; k < i; k++) {
			Model model = aclass30_sub2_sub4_sub6s[k];
			if (model != null) {
				totalVertices += model.totalVertices;
				triangleCount += model.triangleCount;
				anInt1642 += model.anInt1642;
				flag1 |= model.triangleDrawType != null;
				if (model.priorities != null) {
					flag2 = true;
				} else {
					if (anInt1641 == -1)
						anInt1641 = model.anInt1641;
					if (anInt1641 != model.anInt1641)
						flag2 = true;
				}
				flag3 |= model.alpha != null;
				flag4 |= model.colors != null;
			}
		}

		vertexX = new int[totalVertices];
		vertexY = new int[totalVertices];
		vertexZ = new int[totalVertices];
		triangleA = new int[triangleCount];
		triangleB = new int[triangleCount];
		triangleC = new int[triangleCount];
		anIntArray1634 = new int[triangleCount];
		anIntArray1635 = new int[triangleCount];
		anIntArray1636 = new int[triangleCount];
		anIntArray1643 = new int[anInt1642];
		anIntArray1644 = new int[anInt1642];
		anIntArray1645 = new int[anInt1642];
		if (flag1)
			triangleDrawType = new int[triangleCount];
		if (flag2)
			priorities = new int[triangleCount];
		if (flag3)
			alpha = new int[triangleCount];
		if (flag4)
			colors = new int[triangleCount];
		totalVertices = 0;
		triangleCount = 0;
		anInt1642 = 0;
		int i1 = 0;
		for (int j1 = 0; j1 < i; j1++) {
			Model model_1 = aclass30_sub2_sub4_sub6s[j1];
			if (model_1 != null) {
				int k1 = totalVertices;
				for (int l1 = 0; l1 < model_1.totalVertices; l1++) {
					vertexX[totalVertices] = model_1.vertexX[l1];
					vertexY[totalVertices] = model_1.vertexY[l1];
					vertexZ[totalVertices] = model_1.vertexZ[l1];
					totalVertices++;
				}

				for (int i2 = 0; i2 < model_1.triangleCount; i2++) {
					triangleA[triangleCount] = model_1.triangleA[i2] + k1;
					triangleB[triangleCount] = model_1.triangleB[i2] + k1;
					triangleC[triangleCount] = model_1.triangleC[i2] + k1;
					anIntArray1634[triangleCount] = model_1.anIntArray1634[i2];
					anIntArray1635[triangleCount] = model_1.anIntArray1635[i2];
					anIntArray1636[triangleCount] = model_1.anIntArray1636[i2];
					if (flag1)
						if (model_1.triangleDrawType == null) {
							triangleDrawType[triangleCount] = 0;
						} else {
							int j2 = model_1.triangleDrawType[i2];
							if ((j2 & 2) == 2)
								j2 += i1 << 2;
							triangleDrawType[triangleCount] = j2;
						}
					if (flag2)
						if (model_1.priorities == null)
							priorities[triangleCount] = model_1.anInt1641;
						else
							priorities[triangleCount] = model_1.priorities[i2];
					if (flag3)
						if (model_1.alpha == null)
							alpha[triangleCount] = 0;
						else
							alpha[triangleCount] = model_1.alpha[i2];
					if (flag4 && model_1.colors != null)
						colors[triangleCount] = model_1.colors[i2];
					triangleCount++;
				}

				for (int k2 = 0; k2 < model_1.anInt1642; k2++) {
					anIntArray1643[anInt1642] = model_1.anIntArray1643[k2] + k1;
					anIntArray1644[anInt1642] = model_1.anIntArray1644[k2] + k1;
					anIntArray1645[anInt1642] = model_1.anIntArray1645[k2] + k1;
					anInt1642++;
				}

				i1 += model_1.anInt1642;
			}
		}

		method466();
	}

	public Model(boolean flag, boolean flag1, boolean flag2, Model model) {
		aBoolean1659 = false;
		totalVertices = model.totalVertices;
		triangleCount = model.triangleCount;
		anInt1642 = model.anInt1642;
		if (flag2) {
			vertexX = model.vertexX;
			vertexY = model.vertexY;
			vertexZ = model.vertexZ;
		} else {
			vertexX = new int[totalVertices];
			vertexY = new int[totalVertices];
			vertexZ = new int[totalVertices];
			for (int j = 0; j < totalVertices; j++) {
				vertexX[j] = model.vertexX[j];
				vertexY[j] = model.vertexY[j];
				vertexZ[j] = model.vertexZ[j];
			}

		}
		if (flag) {
			colors = model.colors;
		} else {
			colors = new int[triangleCount];
			System.arraycopy(model.colors, 0, colors, 0, triangleCount);
		}
		if (flag1) {
			alpha = model.alpha;
		} else {
			alpha = new int[triangleCount];
			if (model.alpha == null) {
				for (int l = 0; l < triangleCount; l++)
					alpha[l] = 0;

			} else {
				System.arraycopy(model.alpha, 0, alpha, 0,
						triangleCount);

			}
		}
		anIntArray1655 = model.anIntArray1655;
		anIntArray1656 = model.anIntArray1656;
		triangleDrawType = model.triangleDrawType;
		triangleA = model.triangleA;
		triangleB = model.triangleB;
		triangleC = model.triangleC;
		priorities = model.priorities;
		anInt1641 = model.anInt1641;
		anIntArray1643 = model.anIntArray1643;
		anIntArray1644 = model.anIntArray1644;
		anIntArray1645 = model.anIntArray1645;
	}

	public Model(boolean flag, boolean flag1, Model model) {
		aBoolean1659 = false;
		totalVertices = model.totalVertices;
		triangleCount = model.triangleCount;
		anInt1642 = model.anInt1642;
		if (flag) {
			vertexY = new int[totalVertices];
			System.arraycopy(model.vertexY, 0, vertexY, 0,
					totalVertices);

		} else {
			vertexY = model.vertexY;
		}
		if (flag1) {
			anIntArray1634 = new int[triangleCount];
			anIntArray1635 = new int[triangleCount];
			anIntArray1636 = new int[triangleCount];
			for (int k = 0; k < triangleCount; k++) {
				anIntArray1634[k] = model.anIntArray1634[k];
				anIntArray1635[k] = model.anIntArray1635[k];
				anIntArray1636[k] = model.anIntArray1636[k];
			}

			triangleDrawType = new int[triangleCount];
			if (model.triangleDrawType == null) {
				for (int l = 0; l < triangleCount; l++)
					triangleDrawType[l] = 0;

			} else {
				System.arraycopy(model.triangleDrawType, 0, triangleDrawType, 0, triangleCount);
			}
			super.vertexNormals = new VertexNormal[totalVertices];
			for (int index = 0; index < totalVertices; index++) {
				VertexNormal vertex = super.vertexNormals[index] = new VertexNormal();
				VertexNormal vertex_1 = model.vertexNormals[index];
				vertex.x = vertex_1.x;
				vertex.y = vertex_1.y;
				vertex.z = vertex_1.z;
				vertex.magnitude = vertex_1.magnitude;
			}

			vertexNormalOffset = model.vertexNormalOffset;
		} else {
			anIntArray1634 = model.anIntArray1634;
			anIntArray1635 = model.anIntArray1635;
			anIntArray1636 = model.anIntArray1636;
			triangleDrawType = model.triangleDrawType;
		}
		vertexX = model.vertexX;
		vertexZ = model.vertexZ;
		colors = model.colors;
		alpha = model.alpha;
		priorities = model.priorities;
		anInt1641 = model.anInt1641;
		triangleA = model.triangleA;
		triangleB = model.triangleB;
		triangleC = model.triangleC;
		anIntArray1643 = model.anIntArray1643;
		anIntArray1644 = model.anIntArray1644;
		anIntArray1645 = model.anIntArray1645;
		super.modelHeight = model.modelHeight;
		maxY = model.maxY;
		anInt1650 = model.anInt1650;
		anInt1653 = model.anInt1653;
		anInt1652 = model.anInt1652;
		minX = model.minX;
		maxZ = model.maxZ;
		minZ = model.minZ;
		maxX = model.maxX;
	}

	public void method464(Model model, boolean flag) {
		totalVertices = model.totalVertices;
		triangleCount = model.triangleCount;
		anInt1642 = model.anInt1642;
		if (anIntArray1622.length < totalVertices) {
			anIntArray1622 = new int[totalVertices + 100];
			anIntArray1623 = new int[totalVertices + 100];
			anIntArray1624 = new int[totalVertices + 100];
		}
		vertexX = anIntArray1622;
		vertexY = anIntArray1623;
		vertexZ = anIntArray1624;
		for (int k = 0; k < totalVertices; k++) {
			vertexX[k] = model.vertexX[k];
			vertexY[k] = model.vertexY[k];
			vertexZ[k] = model.vertexZ[k];
		}

		if (flag) {
			alpha = model.alpha;
		} else {
			if (anIntArray1625.length < triangleCount)
				anIntArray1625 = new int[triangleCount + 100];
			alpha = anIntArray1625;
			if (model.alpha == null) {
				for (int l = 0; l < triangleCount; l++)
					alpha[l] = 0;

			} else {
				System.arraycopy(model.alpha, 0, alpha, 0,
						triangleCount);

			}
		}
		triangleDrawType = model.triangleDrawType;
		colors = model.colors;
		priorities = model.priorities;
		anInt1641 = model.anInt1641;
		anIntArrayArray1658 = model.anIntArrayArray1658;
		anIntArrayArray1657 = model.anIntArrayArray1657;
		triangleA = model.triangleA;
		triangleB = model.triangleB;
		triangleC = model.triangleC;
		anIntArray1634 = model.anIntArray1634;
		anIntArray1635 = model.anIntArray1635;
		anIntArray1636 = model.anIntArray1636;
		anIntArray1643 = model.anIntArray1643;
		anIntArray1644 = model.anIntArray1644;
		anIntArray1645 = model.anIntArray1645;
	}

	private int method465(Model model, int i) {
		int j = -1;
		int k = model.vertexX[i];
		int l = model.vertexY[i];
		int i1 = model.vertexZ[i];
		for (int j1 = 0; j1 < totalVertices; j1++) {
			if (k != vertexX[j1] || l != vertexY[j1]
					|| i1 != vertexZ[j1])
				continue;
			j = j1;
			break;
		}

		if (j == -1) {
			vertexX[totalVertices] = k;
			vertexY[totalVertices] = l;
			vertexZ[totalVertices] = i1;
			if (model.anIntArray1655 != null)
				anIntArray1655[totalVertices] = model.anIntArray1655[i];
			j = totalVertices++;
		}
		return j;
	}

	public void method466() {
		super.modelHeight = 0;
		anInt1650 = 0;
		maxY = 0;
		for (int i = 0; i < totalVertices; i++) {
			int j = vertexX[i];
			int k = vertexY[i];
			int l = vertexZ[i];
			if (-k > super.modelHeight)
				super.modelHeight = -k;
			if (k > maxY)
				maxY = k;
			int i1 = j * j + l * l;
			if (i1 > anInt1650)
				anInt1650 = i1;
		}
		anInt1650 = (int) (Math.sqrt(anInt1650) + 0.98999999999999999D);
		anInt1653 = (int) (Math.sqrt(anInt1650 * anInt1650 + super.modelHeight
				* super.modelHeight) + 0.98999999999999999D);
		anInt1652 = anInt1653
				+ (int) (Math.sqrt(anInt1650 * anInt1650 + maxY
						* maxY) + 0.98999999999999999D);
	}

	public void method467() {
		super.modelHeight = 0;
		maxY = 0;
		for (int i = 0; i < totalVertices; i++) {
			int j = vertexY[i];
			if (-j > super.modelHeight)
				super.modelHeight = -j;
			if (j > maxY)
				maxY = j;
		}

		anInt1653 = (int) (Math.sqrt(anInt1650 * anInt1650 + super.modelHeight
				* super.modelHeight) + 0.98999999999999999D);
		anInt1652 = anInt1653
				+ (int) (Math.sqrt(anInt1650 * anInt1650 + maxY
						* maxY) + 0.98999999999999999D);
	}

	private void method468() {
		super.modelHeight = 0;
		anInt1650 = 0;
		maxY = 0;
		minX = 0xf423f;
		maxX = 0xfff0bdc1;
		maxZ = 0xfffe7961;
		minZ = 0x1869f;
		for (int j = 0; j < totalVertices; j++) {
			int k = vertexX[j];
			int l = vertexY[j];
			int i1 = vertexZ[j];
			if (k < minX)
				minX = k;
			if (k > maxX)
				maxX = k;
			if (i1 < minZ)
				minZ = i1;
			if (i1 > maxZ)
				maxZ = i1;
			if (-l > super.modelHeight)
				super.modelHeight = -l;
			if (l > maxY)
				maxY = l;
			int j1 = k * k + i1 * i1;
			if (j1 > anInt1650)
				anInt1650 = j1;
		}

		anInt1650 = (int) Math.sqrt(anInt1650);
		anInt1653 = (int) Math.sqrt(anInt1650 * anInt1650 + super.modelHeight
				* super.modelHeight);
		anInt1652 = anInt1653
				+ (int) Math
						.sqrt(anInt1650 * anInt1650 + maxY * maxY);
	}

	public void method469() {
		if (anIntArray1655 != null) {
			int ai[] = new int[256];
			int j = 0;
			for (int l = 0; l < totalVertices; l++) {
				int j1 = anIntArray1655[l];
				ai[j1]++;
				if (j1 > j)
					j = j1;
			}

			anIntArrayArray1657 = new int[j + 1][];
			for (int k1 = 0; k1 <= j; k1++) {
				anIntArrayArray1657[k1] = new int[ai[k1]];
				ai[k1] = 0;
			}

			for (int j2 = 0; j2 < totalVertices; j2++) {
				int l2 = anIntArray1655[j2];
				anIntArrayArray1657[l2][ai[l2]++] = j2;
			}

			anIntArray1655 = null;
		}
		if (anIntArray1656 != null) {
			int ai1[] = new int[256];
			int k = 0;
			for (int i1 = 0; i1 < triangleCount; i1++) {
				int l1 = anIntArray1656[i1];
				ai1[l1]++;
				if (l1 > k)
					k = l1;
			}

			anIntArrayArray1658 = new int[k + 1][];
			for (int i2 = 0; i2 <= k; i2++) {
				anIntArrayArray1658[i2] = new int[ai1[i2]];
				ai1[i2] = 0;
			}

			for (int k2 = 0; k2 < triangleCount; k2++) {
				int i3 = anIntArray1656[k2];
				anIntArrayArray1658[i3][ai1[i3]++] = k2;
			}

			anIntArray1656 = null;
		}
	}

	public void method470(int i) {
		if (anIntArrayArray1657 == null)
			return;
		if (i == -1)
			return;
		FrameReader class36 = FrameReader.getFrames(i);
		if (class36 == null)
			return;
		SkinList class18 = class36.skinList;
		anInt1681 = 0;
		anInt1682 = 0;
		anInt1683 = 0;
		for (int k = 0; k < class36.anInt638; k++) {
			int l = class36.anIntArray639[k];
			method472(class18.anIntArray342[l], class18.anIntArrayArray343[l],
					class36.anIntArray640[k], class36.anIntArray641[k],
					class36.anIntArray642[k]);
		}

	}

	public void method471(int ai[], int j, int k) {
		if (k == -1)
			return;
		if (ai == null || j == -1) {
			method470(k);
			return;
		}
		FrameReader class36 = FrameReader.getFrames(k);
		if (class36 == null)
			return;
		FrameReader class36_1 = FrameReader.getFrames(j);
		if (class36_1 == null) {
			method470(k);
			return;
		}
		SkinList class18 = class36.skinList;
		anInt1681 = 0;
		anInt1682 = 0;
		anInt1683 = 0;
		int l = 0;
		int i1 = ai[l++];
		for (int j1 = 0; j1 < class36.anInt638; j1++) {
			int k1;
			for (k1 = class36.anIntArray639[j1]; k1 > i1; i1 = ai[l++])
				;
			if (k1 != i1 || class18.anIntArray342[k1] == 0)
				method472(class18.anIntArray342[k1],
						class18.anIntArrayArray343[k1],
						class36.anIntArray640[j1], class36.anIntArray641[j1],
						class36.anIntArray642[j1]);
		}

		anInt1681 = 0;
		anInt1682 = 0;
		anInt1683 = 0;
		l = 0;
		i1 = ai[l++];
		for (int l1 = 0; l1 < class36_1.anInt638; l1++) {
			int i2;
			for (i2 = class36_1.anIntArray639[l1]; i2 > i1; i1 = ai[l++])
				;
			if (i2 == i1 || class18.anIntArray342[i2] == 0)
				method472(class18.anIntArray342[i2],
						class18.anIntArrayArray343[i2],
						class36_1.anIntArray640[l1],
						class36_1.anIntArray641[l1],
						class36_1.anIntArray642[l1]);
		}

	}

	private void method472(int i, int ai[], int j, int k, int l) {
		int i1 = ai.length;
		if (i == 0) {
			int j1 = 0;
			anInt1681 = 0;
			anInt1682 = 0;
			anInt1683 = 0;
			for (int k2 = 0; k2 < i1; k2++) {
				int l3 = ai[k2];
				if (l3 < anIntArrayArray1657.length) {
					int ai5[] = anIntArrayArray1657[l3];
					for (int i5 = 0; i5 < ai5.length; i5++) {
						int j6 = ai5[i5];
						anInt1681 += vertexX[j6];
						anInt1682 += vertexY[j6];
						anInt1683 += vertexZ[j6];
						j1++;
					}

				}
			}

			if (j1 > 0) {
				anInt1681 = anInt1681 / j1 + j;
				anInt1682 = anInt1682 / j1 + k;
				anInt1683 = anInt1683 / j1 + l;
				return;
			} else {
				anInt1681 = j;
				anInt1682 = k;
				anInt1683 = l;
				return;
			}
		}
		if (i == 1) {
			for (int k1 = 0; k1 < i1; k1++) {
				int l2 = ai[k1];
				if (l2 < anIntArrayArray1657.length) {
					int ai1[] = anIntArrayArray1657[l2];
					for (int i4 = 0; i4 < ai1.length; i4++) {
						int j5 = ai1[i4];
						vertexX[j5] += j;
						vertexY[j5] += k;
						vertexZ[j5] += l;
					}

				}
			}

			return;
		}
		if (i == 2) {
			for (int l1 = 0; l1 < i1; l1++) {
				int i3 = ai[l1];
				if (i3 < anIntArrayArray1657.length) {
					int ai2[] = anIntArrayArray1657[i3];
					for (int j4 = 0; j4 < ai2.length; j4++) {
						int k5 = ai2[j4];
						vertexX[k5] -= anInt1681;
						vertexY[k5] -= anInt1682;
						vertexZ[k5] -= anInt1683;
						int k6 = (j & 0xff) * 8;
						int l6 = (k & 0xff) * 8;
						int i7 = (l & 0xff) * 8;
						if (i7 != 0) {
							int j7 = SINE[i7];
							int i8 = COSINE[i7];
							int l8 = vertexY[k5] * j7
									+ vertexX[k5] * i8 >> 16;
							vertexY[k5] = vertexY[k5] * i8
									- vertexX[k5] * j7 >> 16;
							vertexX[k5] = l8;
						}
						if (k6 != 0) {
							int k7 = SINE[k6];
							int j8 = COSINE[k6];
							int i9 = vertexY[k5] * j8
									- vertexZ[k5] * k7 >> 16;
							vertexZ[k5] = vertexY[k5] * k7
									+ vertexZ[k5] * j8 >> 16;
							vertexY[k5] = i9;
						}
						if (l6 != 0) {
							int l7 = SINE[l6];
							int k8 = COSINE[l6];
							int j9 = vertexZ[k5] * l7
									+ vertexX[k5] * k8 >> 16;
							vertexZ[k5] = vertexZ[k5] * k8
									- vertexX[k5] * l7 >> 16;
							vertexX[k5] = j9;
						}
						vertexX[k5] += anInt1681;
						vertexY[k5] += anInt1682;
						vertexZ[k5] += anInt1683;
					}

				}
			}

			return;
		}
		if (i == 3) {
			for (int i2 = 0; i2 < i1; i2++) {
				int j3 = ai[i2];
				if (j3 < anIntArrayArray1657.length) {
					int ai3[] = anIntArrayArray1657[j3];
					for (int k4 = 0; k4 < ai3.length; k4++) {
						int l5 = ai3[k4];
						vertexX[l5] -= anInt1681;
						vertexY[l5] -= anInt1682;
						vertexZ[l5] -= anInt1683;
						vertexX[l5] = (vertexX[l5] * j) / 128;
						vertexY[l5] = (vertexY[l5] * k) / 128;
						vertexZ[l5] = (vertexZ[l5] * l) / 128;
						vertexX[l5] += anInt1681;
						vertexY[l5] += anInt1682;
						vertexZ[l5] += anInt1683;
					}

				}
			}

			return;
		}
		if (i == 5 && anIntArrayArray1658 != null && alpha != null) {
			for (int j2 = 0; j2 < i1; j2++) {
				int k3 = ai[j2];
				if (k3 < anIntArrayArray1658.length) {
					int ai4[] = anIntArrayArray1658[k3];
					for (int l4 = 0; l4 < ai4.length; l4++) {
						int i6 = ai4[l4];
						alpha[i6] += j * 8;
						if (alpha[i6] < 0)
							alpha[i6] = 0;
						if (alpha[i6] > 255)
							alpha[i6] = 255;
					}

				}
			}

		}
	}

	public void method473() {
		for (int j = 0; j < totalVertices; j++) {
			int k = vertexX[j];
			vertexX[j] = vertexZ[j];
			vertexZ[j] = -k;
		}

	}

	public void method474(int i) {
		int k = SINE[i];
		int l = COSINE[i];
		for (int i1 = 0; i1 < totalVertices; i1++) {
			int j1 = vertexY[i1] * l - vertexZ[i1] * k >> 16;
			vertexZ[i1] = vertexY[i1] * k + vertexZ[i1]
					* l >> 16;
			vertexY[i1] = j1;
		}
	}

	public void method475(int i, int j, int l) {
		for (int vertex = 0; vertex < totalVertices; vertex++) {
			vertexX[vertex] += i;
			vertexY[vertex] += j;
			vertexZ[vertex] += l;
		}

	}

	public void changeModelColors(int i, int j) {
		for (int color = 0; color < triangleCount; color++) {
			if (colors[color] == i) {
				colors[color] = j;
			}
		}
	}

	public void method477() {
		for (int vertex = 0; vertex < totalVertices; vertex++) {
			vertexZ[vertex] = -vertexZ[vertex];
		}
		for (int triangle = 0; triangle < triangleCount; triangle++) {
			int l = triangleA[triangle];
			triangleA[triangle] = triangleC[triangle];
			triangleC[triangle] = l;
		}
	}

	public void scaleModel(int scaleX, int scaleY, int scaleZ) {
		for (int vertex = 0; vertex < totalVertices; vertex++) {
			vertexX[vertex] = (vertexX[vertex] * scaleX) / 128;
			vertexY[vertex] = (vertexY[vertex] * scaleY) / 128;
			vertexZ[vertex] = (vertexZ[vertex] * scaleZ) / 128;
		}
	}

	public void method479(int i, int j, int k, int l, int i1, boolean flag) {
		int j1 = (int) Math.sqrt(k * k + l * l + i1 * i1);
		int k1 = j * j1 >> 8;
		if (anIntArray1634 == null) {
			anIntArray1634 = new int[triangleCount];
			anIntArray1635 = new int[triangleCount];
			anIntArray1636 = new int[triangleCount];
		}
		if (super.vertexNormals == null) {
			super.vertexNormals = new VertexNormal[totalVertices];
			for (int l1 = 0; l1 < totalVertices; l1++)
				super.vertexNormals[l1] = new VertexNormal();

		}
		for (int i2 = 0; i2 < triangleCount; i2++) {
			int j2 = triangleA[i2];
			int l2 = triangleB[i2];
			int i3 = triangleC[i2];
			int j3 = vertexX[l2] - vertexX[j2];
			int k3 = vertexY[l2] - vertexY[j2];
			int l3 = vertexZ[l2] - vertexZ[j2];
			int i4 = vertexX[i3] - vertexX[j2];
			int j4 = vertexY[i3] - vertexY[j2];
			int k4 = vertexZ[i3] - vertexZ[j2];
			int l4 = k3 * k4 - j4 * l3;
			int i5 = l3 * i4 - k4 * j3;
			int j5;
			for (j5 = j3 * j4 - i4 * k3; l4 > 8192 || i5 > 8192 || j5 > 8192
					|| l4 < -8192 || i5 < -8192 || j5 < -8192; j5 >>= 1) {
				l4 >>= 1;
				i5 >>= 1;
			}

			int k5 = (int) Math.sqrt(l4 * l4 + i5 * i5 + j5 * j5);
			if (k5 <= 0)
				k5 = 1;
			l4 = (l4 * 256) / k5;
			i5 = (i5 * 256) / k5;
			j5 = (j5 * 256) / k5;
			if (triangleDrawType == null || (triangleDrawType[i2] & 1) == 0) {
				VertexNormal vertex = super.vertexNormals[j2];
				vertex.x += l4;
				vertex.y += i5;
				vertex.z += j5;
				vertex.magnitude++;
				vertex = super.vertexNormals[l2];
				vertex.x += l4;
				vertex.y += i5;
				vertex.z += j5;
				vertex.magnitude++;
				vertex = super.vertexNormals[i3];
				vertex.x += l4;
				vertex.y += i5;
				vertex.z += j5;
				vertex.magnitude++;
			} else {
				int l5 = i + (k * l4 + l * i5 + i1 * j5) / (k1 + k1 / 2);
				anIntArray1634[i2] = method481(colors[i2], l5, triangleDrawType[i2]);
			}
		}

		if (flag) {
			doShading(i, k1, k, l, i1);
		} else {
			vertexNormalOffset = new VertexNormal[totalVertices];
			for (int k2 = 0; k2 < totalVertices; k2++) {
				VertexNormal vertex = super.vertexNormals[k2];
				VertexNormal vertex_1 = vertexNormalOffset[k2] = new VertexNormal();
				vertex_1.x = vertex.x;
				vertex_1.y = vertex.y;
				vertex_1.z = vertex.z;
				vertex_1.magnitude = vertex.magnitude;
			}

		}
		if (flag) {
			method466();
		} else {
			method468();
		}
	}

	public void doShading(int i, int j, int k, int l, int i1) {
		for (int j1 = 0; j1 < triangleCount; j1++) {
			int k1 = triangleA[j1];
			int i2 = triangleB[j1];
			int j2 = triangleC[j1];
			if (triangleDrawType == null) {
				int i3 = colors[j1];
				VertexNormal vertex = super.vertexNormals[k1];
				int k2 = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
				anIntArray1634[j1] = method481(i3, k2, 0);
				vertex = super.vertexNormals[i2];
				k2 = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
				anIntArray1635[j1] = method481(i3, k2, 0);
				vertex = super.vertexNormals[j2];
				k2 = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
				anIntArray1636[j1] = method481(i3, k2, 0);
			} else if ((triangleDrawType[j1] & 1) == 0) {
				int j3 = colors[j1];
				int k3 = triangleDrawType[j1];
				VertexNormal vertex = super.vertexNormals[k1];
				int l2 = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
				anIntArray1634[j1] = method481(j3, l2, k3);
				vertex = super.vertexNormals[i2];
				l2 = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
				anIntArray1635[j1] = method481(j3, l2, k3);
				vertex = super.vertexNormals[j2];
				l2 = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
				anIntArray1636[j1] = method481(j3, l2, k3);
			}
		}
		super.vertexNormals = null;
		vertexNormalOffset = null;
		anIntArray1655 = null;
		anIntArray1656 = null;
		if (triangleDrawType != null) {
			for (int triangle = 0; triangle < triangleCount; triangle++) {
				if ((triangleDrawType[triangle] & 2) == 2) {
					return;
				}
			}
		}
		colors = null;
	}

	private static int method481(int i, int j, int k) {
		if ((k & 2) == 2) {
			if (j < 0)
				j = 0;
			else if (j > 127)
				j = 127;
			j = 127 - j;
			return j;
		}
		j = j * (i & 0x7f) >> 7;
		if (j < 2)
			j = 2;
		else if (j > 126)
			j = 126;
		return (i & 0xff80) + j;
	}

	public void method482(int j, int k, int l, int i1, int j1, int k1) {
		int i = 0; // was a parameter
		int l1 = Rasterizer.centerX;
		int i2 = Rasterizer.centerY;
		int j2 = SINE[i];
		int k2 = COSINE[i];
		int l2 = SINE[j];
		int i3 = COSINE[j];
		int j3 = SINE[k];
		int k3 = COSINE[k];
		int l3 = SINE[l];
		int i4 = COSINE[l];
		int j4 = j1 * l3 + k1 * i4 >> 16;
		for (int index = 0; index < totalVertices; index++) {
			int l4 = vertexX[index];
			int i5 = vertexY[index];
			int j5 = vertexZ[index];
			if (k != 0) {
				int k5 = i5 * j3 + l4 * k3 >> 16;
				i5 = i5 * k3 - l4 * j3 >> 16;
				l4 = k5;
			}
			if (i != 0) {
				int l5 = i5 * k2 - j5 * j2 >> 16;
				j5 = i5 * j2 + j5 * k2 >> 16;
				i5 = l5;
			}
			if (j != 0) {
				int i6 = j5 * l2 + l4 * i3 >> 16;
				j5 = j5 * i3 - l4 * l2 >> 16;
				l4 = i6;
			}
			l4 += i1;
			i5 += j1;
			j5 += k1;
			int j6 = i5 * i4 - j5 * l3 >> 16;
			j5 = i5 * l3 + j5 * i4 >> 16;
			i5 = j6;
			anIntArray1667[index] = j5 - j4;
			anIntArray1665[index] = l1 + (l4 << 9) / j5;
			anIntArray1666[index] = i2 + (i5 << 9) / j5;
			if (anInt1642 > 0) {
				anIntArray1668[index] = l4;
				anIntArray1669[index] = i5;
				anIntArray1670[index] = j5;
			}
		}
		try {
			method483(false, false, 0);
		} catch (Exception _ex) {
		}
	}

	public void renderAtPoint(int i, int j, int k, int l, int i1, int j1, int k1,
			int l1, int i2) {
		int j2 = l1 * i1 - j1 * l >> 16;
		int k2 = k1 * j + j2 * k >> 16;
		int l2 = anInt1650 * k >> 16;
		int i3 = k2 + l2;
		if (i3 <= 50 || k2 >= 3500)
			return;
		int j3 = l1 * l + j1 * i1 >> 16;
		int k3 = j3 - anInt1650 << 9;
		if (k3 / i3 >= RSDrawingArea.centerY)
			return;
		int l3 = j3 + anInt1650 << 9;
		if (l3 / i3 <= -RSDrawingArea.centerY)
			return;
		int i4 = k1 * k - j2 * j >> 16;
		int j4 = anInt1650 * j >> 16;
		int k4 = i4 + j4 << 9;
		if (k4 / i3 <= -RSDrawingArea.anInt1387)
			return;
		int l4 = j4 + (super.modelHeight * k >> 16);
		int i5 = i4 - l4 << 9;
		if (i5 / i3 >= RSDrawingArea.anInt1387)
			return;
		int j5 = l2 + (super.modelHeight * j >> 16);
		boolean flag = false;
		if (k2 - j5 <= 50)
			flag = true;
		boolean flag1 = false;
		if (i2 > 0 && aBoolean1684) {
			int k5 = k2 - l2;
			if (k5 <= 50)
				k5 = 50;
			if (j3 > 0) {
				k3 /= i3;
				l3 /= k5;
			} else {
				l3 /= i3;
				k3 /= k5;
			}
			if (i4 > 0) {
				i5 /= i3;
				k4 /= k5;
			} else {
				k4 /= i3;
				i5 /= k5;
			}
			int i6 = anInt1685 - Rasterizer.centerX;
			int k6 = anInt1686 - Rasterizer.centerY;
			if (i6 > k3 && i6 < l3 && k6 > i5 && k6 < k4)
				if (aBoolean1659)
					anIntArray1688[anInt1687++] = i2;
				else
					flag1 = true;
		}
		int l5 = Rasterizer.centerX;
		int j6 = Rasterizer.centerY;
		int l6 = 0;
		int i7 = 0;
		if (i != 0) {
			l6 = SINE[i];
			i7 = COSINE[i];
		}
		for (int j7 = 0; j7 < totalVertices; j7++) {
			int k7 = vertexX[j7];
			int l7 = vertexY[j7];
			int i8 = vertexZ[j7];
			if (i != 0) {
				int j8 = i8 * l6 + k7 * i7 >> 16;
				i8 = i8 * i7 - k7 * l6 >> 16;
				k7 = j8;
			}
			k7 += j1;
			l7 += k1;
			i8 += l1;
			int k8 = i8 * l + k7 * i1 >> 16;
			i8 = i8 * i1 - k7 * l >> 16;
			k7 = k8;
			k8 = l7 * k - i8 * j >> 16;
			i8 = l7 * j + i8 * k >> 16;
			l7 = k8;
			anIntArray1667[j7] = i8 - k2;
			if (i8 >= 50) {
				anIntArray1665[j7] = l5 + (k7 << 9) / i8;
				anIntArray1666[j7] = j6 + (l7 << 9) / i8;
			} else {
				anIntArray1665[j7] = -5000;
				flag = true;
			}
			if (flag || anInt1642 > 0) {
				anIntArray1668[j7] = k7;
				anIntArray1669[j7] = l7;
				anIntArray1670[j7] = i8;
			}
		}

		try {
			method483(flag, flag1, i2);
		} catch (Exception _ex) {
		}
	}

	private void method483(boolean flag, boolean flag1, int i) {
		for (int j = 0; j < anInt1652; j++)
			anIntArray1671[j] = 0;

		for (int k = 0; k < triangleCount; k++)
			if (triangleDrawType == null || triangleDrawType[k] != -1) {
				int l = triangleA[k];
				int k1 = triangleB[k];
				int j2 = triangleC[k];
				int i3 = anIntArray1665[l];
				int l3 = anIntArray1665[k1];
				int k4 = anIntArray1665[j2];
				if (flag && (i3 == -5000 || l3 == -5000 || k4 == -5000)) {
					aBooleanArray1664[k] = true;
					int j5 = (anIntArray1667[l] + anIntArray1667[k1] + anIntArray1667[j2])
							/ 3 + anInt1653;
					anIntArrayArray1672[j5][anIntArray1671[j5]++] = k;
				} else {
					if (flag1
							&& method486(anInt1685, anInt1686,
									anIntArray1666[l], anIntArray1666[k1],
									anIntArray1666[j2], i3, l3, k4)) {
						anIntArray1688[anInt1687++] = i;
						flag1 = false;
					}
					if ((i3 - l3) * (anIntArray1666[j2] - anIntArray1666[k1])
							- (anIntArray1666[l] - anIntArray1666[k1])
							* (k4 - l3) > 0) {
						aBooleanArray1664[k] = false;
						aBooleanArray1663[k] = i3 < 0 || l3 < 0 || k4 < 0
								|| i3 > RSDrawingArea.centerX
								|| l3 > RSDrawingArea.centerX
								|| k4 > RSDrawingArea.centerX;
						int k5 = (anIntArray1667[l] + anIntArray1667[k1] + anIntArray1667[j2])
								/ 3 + anInt1653;
						anIntArrayArray1672[k5][anIntArray1671[k5]++] = k;
					}
				}
			}

		if (priorities == null) {
			for (int i1 = anInt1652 - 1; i1 >= 0; i1--) {
				int l1 = anIntArray1671[i1];
				if (l1 > 0) {
					int ai[] = anIntArrayArray1672[i1];
					for (int j3 = 0; j3 < l1; j3++)
						method484(ai[j3]);

				}
			}

			return;
		}
		for (int j1 = 0; j1 < 12; j1++) {
			anIntArray1673[j1] = 0;
			anIntArray1677[j1] = 0;
		}

		for (int i2 = anInt1652 - 1; i2 >= 0; i2--) {
			int k2 = anIntArray1671[i2];
			if (k2 > 0) {
				int ai1[] = anIntArrayArray1672[i2];
				for (int i4 = 0; i4 < k2; i4++) {
					int l4 = ai1[i4];
					int l5 = priorities[l4];
					int j6 = anIntArray1673[l5]++;
					anIntArrayArray1674[l5][j6] = l4;
					if (l5 < 10)
						anIntArray1677[l5] += i2;
					else if (l5 == 10)
						anIntArray1675[j6] = i2;
					else
						anIntArray1676[j6] = i2;
				}

			}
		}

		int l2 = 0;
		if (anIntArray1673[1] > 0 || anIntArray1673[2] > 0)
			l2 = (anIntArray1677[1] + anIntArray1677[2])
					/ (anIntArray1673[1] + anIntArray1673[2]);
		int k3 = 0;
		if (anIntArray1673[3] > 0 || anIntArray1673[4] > 0)
			k3 = (anIntArray1677[3] + anIntArray1677[4])
					/ (anIntArray1673[3] + anIntArray1673[4]);
		int j4 = 0;
		if (anIntArray1673[6] > 0 || anIntArray1673[8] > 0)
			j4 = (anIntArray1677[6] + anIntArray1677[8])
					/ (anIntArray1673[6] + anIntArray1673[8]);
		int i6 = 0;
		int k6 = anIntArray1673[10];
		int ai2[] = anIntArrayArray1674[10];
		int ai3[] = anIntArray1675;
		if (i6 == k6) {
			i6 = 0;
			k6 = anIntArray1673[11];
			ai2 = anIntArrayArray1674[11];
			ai3 = anIntArray1676;
		}
		int i5;
		if (i6 < k6)
			i5 = ai3[i6];
		else
			i5 = -1000;
		for (int l6 = 0; l6 < 10; l6++) {
			while (l6 == 0 && i5 > l2) {
				method484(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6)
					i5 = ai3[i6];
				else
					i5 = -1000;
			}
			while (l6 == 3 && i5 > k3) {
				method484(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6)
					i5 = ai3[i6];
				else
					i5 = -1000;
			}
			while (l6 == 5 && i5 > j4) {
				method484(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6)
					i5 = ai3[i6];
				else
					i5 = -1000;
			}
			int i7 = anIntArray1673[l6];
			int ai4[] = anIntArrayArray1674[l6];
			for (int j7 = 0; j7 < i7; j7++)
				method484(ai4[j7]);

		}

		while (i5 != -1000) {
			method484(ai2[i6++]);
			if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
				i6 = 0;
				ai2 = anIntArrayArray1674[11];
				k6 = anIntArray1673[11];
				ai3 = anIntArray1676;
			}
			if (i6 < k6)
				i5 = ai3[i6];
			else
				i5 = -1000;
		}
	}

	private void method484(int i) {
		if (aBooleanArray1664[i]) {
			method485(i);
			return;
		}
		int j = triangleA[i];
		int k = triangleB[i];
		int l = triangleC[i];
		Rasterizer.restrictEdges = aBooleanArray1663[i];
		if (alpha == null)
			Rasterizer.alpha = 0;
		else
			Rasterizer.alpha = alpha[i];
		int i1;
		if (triangleDrawType == null)
			i1 = 0;
		else
			i1 = triangleDrawType[i] & 3;
		if (i1 == 0) {
			Rasterizer.drawShadedTriangle(anIntArray1666[j], anIntArray1666[k],
					anIntArray1666[l], anIntArray1665[j], anIntArray1665[k],
					anIntArray1665[l], anIntArray1634[i], anIntArray1635[i],
					anIntArray1636[i]);
			return;
		}
		if (i1 == 1) {
			Rasterizer.drawFlatTriangle(anIntArray1666[j], anIntArray1666[k],
					anIntArray1666[l], anIntArray1665[j], anIntArray1665[k],
					anIntArray1665[l], modelIntArray3[anIntArray1634[i]]);
			return;
		}
		if (i1 == 2) {
			int j1 = triangleDrawType[i] >> 2;
			int l1 = anIntArray1643[j1];
			int j2 = anIntArray1644[j1];
			int l2 = anIntArray1645[j1];
			Rasterizer.drawTexturedTriangle(anIntArray1666[j], anIntArray1666[k],
					anIntArray1666[l], anIntArray1665[j], anIntArray1665[k],
					anIntArray1665[l], anIntArray1634[i], anIntArray1635[i],
					anIntArray1636[i], anIntArray1668[l1], anIntArray1668[j2],
					anIntArray1668[l2], anIntArray1669[l1], anIntArray1669[j2],
					anIntArray1669[l2], anIntArray1670[l1], anIntArray1670[j2],
					anIntArray1670[l2], colors[i]);
			return;
		}
		if (i1 == 3) {
			int k1 = triangleDrawType[i] >> 2;
			int i2 = anIntArray1643[k1];
			int k2 = anIntArray1644[k1];
			int i3 = anIntArray1645[k1];
			Rasterizer.drawTexturedTriangle(anIntArray1666[j], anIntArray1666[k],
					anIntArray1666[l], anIntArray1665[j], anIntArray1665[k],
					anIntArray1665[l], anIntArray1634[i], anIntArray1634[i],
					anIntArray1634[i], anIntArray1668[i2], anIntArray1668[k2],
					anIntArray1668[i3], anIntArray1669[i2], anIntArray1669[k2],
					anIntArray1669[i3], anIntArray1670[i2], anIntArray1670[k2],
					anIntArray1670[i3], colors[i]);
		}
	}

	private void method485(int i) {
		int j = Rasterizer.centerX;
		int k = Rasterizer.centerY;
		int l = 0;
		int i1 = triangleA[i];
		int j1 = triangleB[i];
		int k1 = triangleC[i];
		int l1 = anIntArray1670[i1];
		int i2 = anIntArray1670[j1];
		int j2 = anIntArray1670[k1];
		if (l1 >= 50) {
			anIntArray1678[l] = anIntArray1665[i1];
			anIntArray1679[l] = anIntArray1666[i1];
			anIntArray1680[l++] = anIntArray1634[i];
		} else {
			int k2 = anIntArray1668[i1];
			int k3 = anIntArray1669[i1];
			int k4 = anIntArray1634[i];
			if (j2 >= 50) {
				int k5 = (50 - l1) * modelIntArray4[j2 - l1];
				anIntArray1678[l] = j
						+ (k2 + ((anIntArray1668[k1] - k2) * k5 >> 16) << 9)
						/ 50;
				anIntArray1679[l] = k
						+ (k3 + ((anIntArray1669[k1] - k3) * k5 >> 16) << 9)
						/ 50;
				anIntArray1680[l++] = k4
						+ ((anIntArray1636[i] - k4) * k5 >> 16);
			}
			if (i2 >= 50) {
				int l5 = (50 - l1) * modelIntArray4[i2 - l1];
				anIntArray1678[l] = j
						+ (k2 + ((anIntArray1668[j1] - k2) * l5 >> 16) << 9)
						/ 50;
				anIntArray1679[l] = k
						+ (k3 + ((anIntArray1669[j1] - k3) * l5 >> 16) << 9)
						/ 50;
				anIntArray1680[l++] = k4
						+ ((anIntArray1635[i] - k4) * l5 >> 16);
			}
		}
		if (i2 >= 50) {
			anIntArray1678[l] = anIntArray1665[j1];
			anIntArray1679[l] = anIntArray1666[j1];
			anIntArray1680[l++] = anIntArray1635[i];
		} else {
			int l2 = anIntArray1668[j1];
			int l3 = anIntArray1669[j1];
			int l4 = anIntArray1635[i];
			if (l1 >= 50) {
				int i6 = (50 - i2) * modelIntArray4[l1 - i2];
				anIntArray1678[l] = j
						+ (l2 + ((anIntArray1668[i1] - l2) * i6 >> 16) << 9)
						/ 50;
				anIntArray1679[l] = k
						+ (l3 + ((anIntArray1669[i1] - l3) * i6 >> 16) << 9)
						/ 50;
				anIntArray1680[l++] = l4
						+ ((anIntArray1634[i] - l4) * i6 >> 16);
			}
			if (j2 >= 50) {
				int j6 = (50 - i2) * modelIntArray4[j2 - i2];
				anIntArray1678[l] = j
						+ (l2 + ((anIntArray1668[k1] - l2) * j6 >> 16) << 9)
						/ 50;
				anIntArray1679[l] = k
						+ (l3 + ((anIntArray1669[k1] - l3) * j6 >> 16) << 9)
						/ 50;
				anIntArray1680[l++] = l4
						+ ((anIntArray1636[i] - l4) * j6 >> 16);
			}
		}
		if (j2 >= 50) {
			anIntArray1678[l] = anIntArray1665[k1];
			anIntArray1679[l] = anIntArray1666[k1];
			anIntArray1680[l++] = anIntArray1636[i];
		} else {
			int i3 = anIntArray1668[k1];
			int i4 = anIntArray1669[k1];
			int i5 = anIntArray1636[i];
			if (i2 >= 50) {
				int k6 = (50 - j2) * modelIntArray4[i2 - j2];
				anIntArray1678[l] = j
						+ (i3 + ((anIntArray1668[j1] - i3) * k6 >> 16) << 9)
						/ 50;
				anIntArray1679[l] = k
						+ (i4 + ((anIntArray1669[j1] - i4) * k6 >> 16) << 9)
						/ 50;
				anIntArray1680[l++] = i5
						+ ((anIntArray1635[i] - i5) * k6 >> 16);
			}
			if (l1 >= 50) {
				int l6 = (50 - j2) * modelIntArray4[l1 - j2];
				anIntArray1678[l] = j
						+ (i3 + ((anIntArray1668[i1] - i3) * l6 >> 16) << 9)
						/ 50;
				anIntArray1679[l] = k
						+ (i4 + ((anIntArray1669[i1] - i4) * l6 >> 16) << 9)
						/ 50;
				anIntArray1680[l++] = i5
						+ ((anIntArray1634[i] - i5) * l6 >> 16);
			}
		}
		int j3 = anIntArray1678[0];
		int j4 = anIntArray1678[1];
		int j5 = anIntArray1678[2];
		int i7 = anIntArray1679[0];
		int j7 = anIntArray1679[1];
		int k7 = anIntArray1679[2];
		if ((j3 - j4) * (k7 - j7) - (i7 - j7) * (j5 - j4) > 0) {
			Rasterizer.restrictEdges = false;
			if (l == 3) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > RSDrawingArea.centerX
						|| j4 > RSDrawingArea.centerX || j5 > RSDrawingArea.centerX)
					Rasterizer.restrictEdges = true;
				int l7;
				if (triangleDrawType == null)
					l7 = 0;
				else
					l7 = triangleDrawType[i] & 3;
				if (l7 == 0)
					Rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5,
							anIntArray1680[0], anIntArray1680[1],
							anIntArray1680[2]);
				else if (l7 == 1)
					Rasterizer.drawFlatTriangle(i7, j7, k7, j3, j4, j5,
							modelIntArray3[anIntArray1634[i]]);
				else if (l7 == 2) {
					int j8 = triangleDrawType[i] >> 2;
					int k9 = anIntArray1643[j8];
					int k10 = anIntArray1644[j8];
					int k11 = anIntArray1645[j8];
					Rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5,
							anIntArray1680[0], anIntArray1680[1],
							anIntArray1680[2], anIntArray1668[k9],
							anIntArray1668[k10], anIntArray1668[k11],
							anIntArray1669[k9], anIntArray1669[k10],
							anIntArray1669[k11], anIntArray1670[k9],
							anIntArray1670[k10], anIntArray1670[k11],
							colors[i]);
				} else if (l7 == 3) {
					int k8 = triangleDrawType[i] >> 2;
					int l9 = anIntArray1643[k8];
					int l10 = anIntArray1644[k8];
					int l11 = anIntArray1645[k8];
					Rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5,
							anIntArray1634[i], anIntArray1634[i],
							anIntArray1634[i], anIntArray1668[l9],
							anIntArray1668[l10], anIntArray1668[l11],
							anIntArray1669[l9], anIntArray1669[l10],
							anIntArray1669[l11], anIntArray1670[l9],
							anIntArray1670[l10], anIntArray1670[l11],
							colors[i]);
				}
			}
			if (l == 4) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > RSDrawingArea.centerX
						|| j4 > RSDrawingArea.centerX || j5 > RSDrawingArea.centerX
						|| anIntArray1678[3] < 0
						|| anIntArray1678[3] > RSDrawingArea.centerX)
					Rasterizer.restrictEdges = true;
				int i8;
				if (triangleDrawType == null)
					i8 = 0;
				else
					i8 = triangleDrawType[i] & 3;
				if (i8 == 0) {
					Rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5,
							anIntArray1680[0], anIntArray1680[1],
							anIntArray1680[2]);
					Rasterizer.drawShadedTriangle(i7, k7, anIntArray1679[3], j3, j5,
							anIntArray1678[3], anIntArray1680[0],
							anIntArray1680[2], anIntArray1680[3]);
					return;
				}
				if (i8 == 1) {
					int l8 = modelIntArray3[anIntArray1634[i]];
					Rasterizer.drawFlatTriangle(i7, j7, k7, j3, j4, j5, l8);
					Rasterizer.drawFlatTriangle(i7, k7, anIntArray1679[3], j3, j5,
							anIntArray1678[3], l8);
					return;
				}
				if (i8 == 2) {
					int i9 = triangleDrawType[i] >> 2;
					int i10 = anIntArray1643[i9];
					int i11 = anIntArray1644[i9];
					int i12 = anIntArray1645[i9];
					Rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5,
							anIntArray1680[0], anIntArray1680[1],
							anIntArray1680[2], anIntArray1668[i10],
							anIntArray1668[i11], anIntArray1668[i12],
							anIntArray1669[i10], anIntArray1669[i11],
							anIntArray1669[i12], anIntArray1670[i10],
							anIntArray1670[i11], anIntArray1670[i12],
							colors[i]);
					Rasterizer.drawTexturedTriangle(i7, k7, anIntArray1679[3], j3, j5,
							anIntArray1678[3], anIntArray1680[0],
							anIntArray1680[2], anIntArray1680[3],
							anIntArray1668[i10], anIntArray1668[i11],
							anIntArray1668[i12], anIntArray1669[i10],
							anIntArray1669[i11], anIntArray1669[i12],
							anIntArray1670[i10], anIntArray1670[i11],
							anIntArray1670[i12], colors[i]);
					return;
				}
				if (i8 == 3) {
					int j9 = triangleDrawType[i] >> 2;
					int j10 = anIntArray1643[j9];
					int j11 = anIntArray1644[j9];
					int j12 = anIntArray1645[j9];
					Rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5,
							anIntArray1634[i], anIntArray1634[i],
							anIntArray1634[i], anIntArray1668[j10],
							anIntArray1668[j11], anIntArray1668[j12],
							anIntArray1669[j10], anIntArray1669[j11],
							anIntArray1669[j12], anIntArray1670[j10],
							anIntArray1670[j11], anIntArray1670[j12],
							colors[i]);
					Rasterizer.drawTexturedTriangle(i7, k7, anIntArray1679[3], j3, j5,
							anIntArray1678[3], anIntArray1634[i],
							anIntArray1634[i], anIntArray1634[i],
							anIntArray1668[j10], anIntArray1668[j11],
							anIntArray1668[j12], anIntArray1669[j10],
							anIntArray1669[j11], anIntArray1669[j12],
							anIntArray1670[j10], anIntArray1670[j11],
							anIntArray1670[j12], colors[i]);
				}
			}
		}
	}

	private boolean method486(int i, int j, int k, int l, int i1, int j1,
			int k1, int l1) {
		if (j < k && j < l && j < i1)
			return false;
		if (j > k && j > l && j > i1)
			return false;
		return !(i < j1 && i < k1 && i < l1) && (i <= j1 || i <= k1 || i <= l1);
	}

	public static final Model aModel_1621 = new Model();
	private static int[] anIntArray1622 = new int[2000];
	private static int[] anIntArray1623 = new int[2000];
	private static int[] anIntArray1624 = new int[2000];
	private static int[] anIntArray1625 = new int[2000];
	public int totalVertices;
	public int vertexX[];
	public int vertexY[];
	public int vertexZ[];
	public int triangleCount;
	public int triangleA[];
	public int triangleB[];
	public int triangleC[];
	private int[] anIntArray1634;
	private int[] anIntArray1635;
	private int[] anIntArray1636;
	public int triangleDrawType[];
	private int[] priorities;
	private int[] alpha;
	public int colors[];
	private int anInt1641;
	private int anInt1642;
	private int[] anIntArray1643;
	private int[] anIntArray1644;
	private int[] anIntArray1645;
	public int minX;
	public int maxX;
	public int maxZ;
	public int minZ;
	public int anInt1650;
	public int maxY;
	private int anInt1652;
	private int anInt1653;
	public int anInt1654;
	private int[] anIntArray1655;
	private int[] anIntArray1656;
	public int anIntArrayArray1657[][];
	public int anIntArrayArray1658[][];
	public boolean aBoolean1659;
	public VertexNormal vertexNormalOffset[];
	private static ModelHeader[] aClass21Array1661;
	private static ModelProvider aOnDemandFetcherParent_1662;
	private static boolean[] aBooleanArray1663 = new boolean[4096];
	private static boolean[] aBooleanArray1664 = new boolean[4096];
	private static int[] anIntArray1665 = new int[4096];
	private static int[] anIntArray1666 = new int[4096];
	private static int[] anIntArray1667 = new int[4096];
	private static int[] anIntArray1668 = new int[4096];
	private static int[] anIntArray1669 = new int[4096];
	private static int[] anIntArray1670 = new int[4096];
	private static int[] anIntArray1671 = new int[1500];
	private static int[][] anIntArrayArray1672 = new int[1500][512];
	private static int[] anIntArray1673 = new int[12];
	private static int[][] anIntArrayArray1674 = new int[12][2000];
	private static int[] anIntArray1675 = new int[2000];
	private static int[] anIntArray1676 = new int[2000];
	private static int[] anIntArray1677 = new int[12];
	private static final int[] anIntArray1678 = new int[10];
	private static final int[] anIntArray1679 = new int[10];
	private static final int[] anIntArray1680 = new int[10];
	private static int anInt1681;
	private static int anInt1682;
	private static int anInt1683;
	public static boolean aBoolean1684;
	public static int anInt1685;
	public static int anInt1686;
	public static int anInt1687;
	public static final int[] anIntArray1688 = new int[1000];
	public static int SINE[];
	public static int COSINE[];
	private static int[] modelIntArray3;
	private static int[] modelIntArray4;

	static {
		SINE = Rasterizer.SINE;
		COSINE = Rasterizer.COSINE;
		modelIntArray3 = Rasterizer.hslToRGB;
		modelIntArray4 = Rasterizer.anIntArray1469;
	}
}
