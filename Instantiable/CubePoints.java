package Reika.DragonAPI.Instantiable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.GridDistortion.OffsetGroup;

public class CubePoints {

	public final CubeVertex x1y1z1;
	public final CubeVertex x2y1z1;
	public final CubeVertex x1y1z2;
	public final CubeVertex x2y1z2;

	public final CubeVertex x1y2z1;
	public final CubeVertex x2y2z1;
	public final CubeVertex x1y2z2;
	public final CubeVertex x2y2z2;

	private final HashMap<String, CubeVertex> vertices = new HashMap();

	public CubePoints(Vec3 x1y1z1, Vec3 x2y1z1, Vec3 x1y1z2, Vec3 x2y1z2, Vec3 x1y2z1, Vec3 x2y2z1, Vec3 x1y2z2, Vec3 x2y2z2) {
		this.x1y1z1 = new CubeVertex("111", x1y1z1);
		this.x2y1z1 = new CubeVertex("211", x2y1z1);
		this.x1y1z2 = new CubeVertex("112", x1y1z2);
		this.x2y1z2 = new CubeVertex("212", x2y1z2);

		this.x1y2z1 = new CubeVertex("121", x1y2z1);
		this.x2y2z1 = new CubeVertex("221", x2y2z1);
		this.x1y2z2 = new CubeVertex("122", x1y2z2);
		this.x2y2z2 = new CubeVertex("222", x2y2z2);
	}

	private CubePoints(CubeVertex x1y1z1, CubeVertex x2y1z1, CubeVertex x1y1z2, CubeVertex x2y1z2, CubeVertex x1y2z1, CubeVertex x2y2z1, CubeVertex x1y2z2, CubeVertex x2y2z2) {
		this.x1y1z1 = new CubeVertex(x1y1z1);
		this.x2y1z1 = new CubeVertex(x2y1z1);
		this.x1y1z2 = new CubeVertex(x1y1z2);
		this.x2y1z2 = new CubeVertex(x2y1z2);

		this.x1y2z1 = new CubeVertex(x1y2z1);
		this.x2y2z1 = new CubeVertex(x2y2z1);
		this.x1y2z2 = new CubeVertex(x1y2z2);
		this.x2y2z2 = new CubeVertex(x2y2z2);
	}

	public Vec3 getCenter() {
		Vec3 vec = Vec3.createVectorHelper(0, 0, 0);
		for (CubeVertex cv : vertices.values()) {
			vec.xCoord += cv.position.xCoord;
			vec.yCoord += cv.position.yCoord;
			vec.zCoord += cv.position.zCoord;
		}
		vec.xCoord /= vertices.size();
		vec.yCoord /= vertices.size();
		vec.zCoord /= vertices.size();
		return vec;
	}

	public void applyOffset(ForgeDirection side, OffsetGroup off) {
		//Random rand = new Random(Minecraft.getMinecraft().theWorld.getTotalWorldTime());
		//rand.nextBoolean();
		//double amt = 0.125*0;//0.0625+rand.nextDouble()*0.3125;
		switch(side) {
			case DOWN:
				x1y1z1.position.xCoord += off.offsetAMM;
				x2y1z1.position.xCoord += off.offsetAPM;
				x1y1z2.position.xCoord += off.offsetAMP;
				x2y1z2.position.xCoord += off.offsetAPP;
				x1y1z1.position.zCoord += off.offsetBMM;
				x2y1z1.position.zCoord += off.offsetBPM;
				x1y1z2.position.zCoord += off.offsetBMP;
				x2y1z2.position.zCoord += off.offsetBPP;
				break;
			case UP:
				x1y2z1.position.xCoord += off.offsetAMM;
				x2y2z1.position.xCoord += off.offsetAPM;
				x1y2z2.position.xCoord += off.offsetAMP;
				x2y2z2.position.xCoord += off.offsetAPP;
				x1y2z1.position.zCoord += off.offsetBMM;
				x2y2z1.position.zCoord += off.offsetBPM;
				x1y2z2.position.zCoord += off.offsetBMP;
				x2y2z2.position.zCoord += off.offsetBPP;
				break;
			case WEST:
				x1y1z1.position.yCoord += off.offsetAMM;
				x1y2z1.position.yCoord += off.offsetAMP;
				x1y1z2.position.yCoord += off.offsetAPM;
				x1y2z2.position.yCoord += off.offsetAPP;
				x1y1z1.position.zCoord += off.offsetBMM;
				x1y2z1.position.zCoord += off.offsetBMP;
				x1y1z2.position.zCoord += off.offsetBPM;
				x1y2z2.position.zCoord += off.offsetBPP;
				break;
			case EAST:
				x2y1z1.position.yCoord += off.offsetAMM;
				x2y2z1.position.yCoord += off.offsetAMP;
				x2y1z2.position.yCoord += off.offsetAPM;
				x2y2z2.position.yCoord += off.offsetAPP;
				x2y1z1.position.zCoord += off.offsetBMM;
				x2y2z1.position.zCoord += off.offsetBMP;
				x2y1z2.position.zCoord += off.offsetBPM;
				x2y2z2.position.zCoord += off.offsetBPP;
				break;
			case NORTH:
				x1y1z1.position.xCoord += off.offsetAMM;
				x2y1z1.position.xCoord += off.offsetAPM;
				x1y2z1.position.xCoord += off.offsetAMP;
				x2y2z1.position.xCoord += off.offsetAPP;
				x1y1z1.position.yCoord += off.offsetBMM;
				x2y1z1.position.yCoord += off.offsetBPM;
				x1y2z1.position.yCoord += off.offsetBMP;
				x2y2z1.position.yCoord += off.offsetBPP;
				break;
			case SOUTH:
				x1y1z2.position.xCoord += off.offsetAMM;
				x2y1z2.position.xCoord += off.offsetAPM;
				x1y2z2.position.xCoord += off.offsetAMP;
				x2y2z2.position.xCoord += off.offsetAPP;
				x1y1z2.position.yCoord += off.offsetBMM;
				x2y1z2.position.yCoord += off.offsetBPM;
				x1y2z2.position.yCoord += off.offsetBMP;
				x2y2z2.position.yCoord += off.offsetBPP;
				break;
			default:
				break;
		}
	}

	public void clamp() {
		x1y1z1.position.xCoord = MathHelper.clamp_double(x1y1z1.position.xCoord, 0, 1);
		x1y1z1.position.yCoord = MathHelper.clamp_double(x1y1z1.position.yCoord, 0, 1);
		x1y1z1.position.zCoord = MathHelper.clamp_double(x1y1z1.position.zCoord, 0, 1);
		x2y1z1.position.xCoord = MathHelper.clamp_double(x2y1z1.position.xCoord, 0, 1);
		x2y1z1.position.yCoord = MathHelper.clamp_double(x2y1z1.position.yCoord, 0, 1);
		x2y1z1.position.zCoord = MathHelper.clamp_double(x2y1z1.position.zCoord, 0, 1);
		x1y1z2.position.xCoord = MathHelper.clamp_double(x1y1z2.position.xCoord, 0, 1);
		x1y1z2.position.yCoord = MathHelper.clamp_double(x1y1z2.position.yCoord, 0, 1);
		x1y1z2.position.zCoord = MathHelper.clamp_double(x1y1z2.position.zCoord, 0, 1);
		x2y1z2.position.xCoord = MathHelper.clamp_double(x2y1z2.position.xCoord, 0, 1);
		x2y1z2.position.yCoord = MathHelper.clamp_double(x2y1z2.position.yCoord, 0, 1);
		x2y1z2.position.zCoord = MathHelper.clamp_double(x2y1z2.position.zCoord, 0, 1);
		x1y2z1.position.xCoord = MathHelper.clamp_double(x1y2z1.position.xCoord, 0, 1);
		x1y2z1.position.yCoord = MathHelper.clamp_double(x1y2z1.position.yCoord, 0, 1);
		x1y2z1.position.zCoord = MathHelper.clamp_double(x1y2z1.position.zCoord, 0, 1);
		x2y2z1.position.xCoord = MathHelper.clamp_double(x2y2z1.position.xCoord, 0, 1);
		x2y2z1.position.yCoord = MathHelper.clamp_double(x2y2z1.position.yCoord, 0, 1);
		x2y2z1.position.zCoord = MathHelper.clamp_double(x2y2z1.position.zCoord, 0, 1);
		x1y2z2.position.xCoord = MathHelper.clamp_double(x1y2z2.position.xCoord, 0, 1);
		x1y2z2.position.yCoord = MathHelper.clamp_double(x1y2z2.position.yCoord, 0, 1);
		x1y2z2.position.zCoord = MathHelper.clamp_double(x1y2z2.position.zCoord, 0, 1);
		x2y2z2.position.xCoord = MathHelper.clamp_double(x2y2z2.position.xCoord, 0, 1);
		x2y2z2.position.yCoord = MathHelper.clamp_double(x2y2z2.position.yCoord, 0, 1);
		x2y2z2.position.zCoord = MathHelper.clamp_double(x2y2z2.position.zCoord, 0, 1);
	}

	public void expand(double amt) {
		this.expand(amt, amt, amt);
	}

	public void expand(double x, double y, double z) {
		x1y1z1.position.xCoord -= x;
		x1y1z1.position.yCoord -= y;
		x1y1z1.position.zCoord -= z;

		x2y1z1.position.xCoord += x;
		x2y1z1.position.yCoord -= y;
		x2y1z1.position.zCoord -= z;

		x1y2z1.position.xCoord -= x;
		x1y2z1.position.yCoord += y;
		x1y2z1.position.zCoord -= z;

		x2y2z1.position.xCoord += x;
		x2y2z1.position.yCoord += y;
		x2y2z1.position.zCoord -= z;

		x1y1z2.position.xCoord -= x;
		x1y1z2.position.yCoord -= y;
		x1y1z2.position.zCoord += z;

		x2y1z2.position.xCoord += x;
		x2y1z2.position.yCoord -= y;
		x2y1z2.position.zCoord += z;

		x1y2z2.position.xCoord -= x;
		x1y2z2.position.yCoord += y;
		x1y2z2.position.zCoord += z;

		x2y2z2.position.xCoord += x;
		x2y2z2.position.yCoord += y;
		x2y2z2.position.zCoord += z;
	}

	public void setSidePosition(ForgeDirection side, double val) {
		switch(side) {
			case DOWN:
				x1y1z1.position.yCoord = val;
				x2y1z1.position.yCoord = val;
				x2y1z2.position.yCoord = val;
				x1y1z2.position.yCoord = val;
				break;
			case UP:
				x1y2z1.position.yCoord = val;
				x2y2z1.position.yCoord = val;
				x2y2z2.position.yCoord = val;
				x1y2z2.position.yCoord = val;
				break;
			case WEST:
				x1y1z1.position.xCoord = val;
				x1y2z1.position.xCoord = val;
				x1y2z2.position.xCoord = val;
				x1y1z2.position.xCoord = val;
				break;
			case EAST:
				x2y1z1.position.xCoord = val;
				x2y2z1.position.xCoord = val;
				x2y2z2.position.xCoord = val;
				x2y1z2.position.xCoord = val;
				break;
			case NORTH:
				x1y1z1.position.zCoord = val;
				x1y2z1.position.zCoord = val;
				x2y2z1.position.zCoord = val;
				x2y1z1.position.zCoord = val;
				break;
			case SOUTH:
				x1y1z2.position.zCoord = val;
				x1y2z2.position.zCoord = val;
				x2y2z2.position.zCoord = val;
				x2y1z2.position.zCoord = val;
				break;
			default:
				break;
		}
	}

	public CubePoints copy() {
		return new CubePoints(x1y1z1, x2y1z1, x1y1z2, x2y1z2, x1y2z1, x2y2z1, x1y2z2, x2y2z2);
	}

	public Collection<CubeVertex> getVertices() {
		return Collections.unmodifiableCollection(vertices.values());
	}

	public CubeVertex getVertex(String id) {
		return vertices.get(id);
	}

	/*
	@SideOnly(Side.CLIENT)
	public double getTextureU(IIcon ico, ForgeDirection side) {
		switch(side) {
			case DOWN:
				return ico.getInterpolatedU(16*x1y1z1.position.xCoord);
			case UP:
				//ReikaJavaLibrary.pConsole(x1y2z1.position);
				return ico.getInterpolatedU(16*x1y2z1.position.xCoord);
			case WEST:
				return ico.getInterpolatedU(16*x1y1z1.position.zCoord);
			case EAST:
				return ico.getInterpolatedU(16*x2y1z1.position.zCoord);
			case NORTH:
				return ico.getInterpolatedU(16*x1y1z1.position.xCoord);
			case SOUTH:
				return ico.getInterpolatedU(16*x1y1z2.position.xCoord);
			default:
				return 0;
		}
	}

	@SideOnly(Side.CLIENT)
	public double getTextureDU(IIcon ico, ForgeDirection side) {
		switch(side) {
			case DOWN:
				return ico.getInterpolatedU(16*x2y1z1.position.xCoord);
			case UP:
				return ico.getInterpolatedU(16*x2y2z1.position.xCoord);
			case WEST:
				return ico.getInterpolatedU(16*x1y1z2.position.zCoord);
			case EAST:
				return ico.getInterpolatedU(16*x2y1z2.position.zCoord);
			case NORTH:
				return ico.getInterpolatedU(16*x2y1z1.position.xCoord);
			case SOUTH:
				return ico.getInterpolatedU(16*x2y1z2.position.xCoord);
			default:
				return 0;
		}
	}

	@SideOnly(Side.CLIENT)
	public double getTextureV(IIcon ico, ForgeDirection side) {
		switch(side) {
			case DOWN:
				return ico.getInterpolatedV(16*x1y1z1.position.zCoord);
			case UP:
				return ico.getInterpolatedV(16*x1y2z1.position.zCoord);
			case WEST:
				return ico.getInterpolatedV(16*x1y1z1.position.yCoord);
			case EAST:
				return ico.getInterpolatedV(16*x2y1z1.position.yCoord);
			case NORTH:
				return ico.getInterpolatedV(16*x1y1z1.position.yCoord);
			case SOUTH:
				return ico.getInterpolatedV(16*x1y1z2.position.yCoord);
			default:
				return 0;
		}
	}

	@SideOnly(Side.CLIENT)
	public double getTextureDV(IIcon ico, ForgeDirection side) {
		switch(side) {
			case DOWN:
				return ico.getInterpolatedV(16*x1y1z2.position.zCoord);
			case UP:
				return ico.getInterpolatedV(16*x1y2z2.position.zCoord);
			case WEST:
				return ico.getInterpolatedV(16*x1y2z1.position.yCoord);
			case EAST:
				return ico.getInterpolatedV(16*x2y2z1.position.yCoord);
			case NORTH:
				return ico.getInterpolatedV(16*x1y2z1.position.yCoord);
			case SOUTH:
				return ico.getInterpolatedV(16*x1y2z2.position.yCoord);
			default:
				return 0;
		}
	}*/

	public final class CubeVertex {

		private final Vec3 position;
		public final String ID;

		private CubeVertex(String id, Vec3 pos) {
			position = pos;
			ID = id;
			this.parent().vertices.put(id, this);
		}

		private CubeVertex(CubeVertex pos) {
			this(pos.ID, Vec3.createVectorHelper(pos.position.xCoord, pos.position.yCoord, pos.position.zCoord));
		}

		public double textureU(IIcon icon, ForgeDirection side) {
			switch(side) {
				case DOWN:
					return icon.getInterpolatedU(16*position.xCoord);
				case UP:
					//ReikaJavaLibrary.pConsole(x1y2z1.position);
					return icon.getInterpolatedU(16*position.xCoord);
				case WEST:
					return icon.getInterpolatedU(16*position.zCoord);
				case EAST:
					return icon.getInterpolatedU(16*position.zCoord);
				case NORTH:
					return icon.getInterpolatedU(16*position.xCoord);
				case SOUTH:
					return icon.getInterpolatedU(16*position.xCoord);
				default:
					return 0;
			}
		}

		public double textureV(IIcon icon, ForgeDirection side) {
			switch(side) {
				case DOWN:
					return icon.getInterpolatedV(16*position.zCoord);
				case UP:
					return icon.getInterpolatedV(16*position.zCoord);
				case WEST:
					return icon.getInterpolatedV(16*position.yCoord);
				case EAST:
					return icon.getInterpolatedV(16*position.yCoord);
				case NORTH:
					return icon.getInterpolatedV(16*position.yCoord);
				case SOUTH:
					return icon.getInterpolatedV(16*position.yCoord);
				default:
					return 0;
			}
		}

		public void draw(Tessellator v5, IIcon ico, ForgeDirection side) {
			v5.addVertexWithUV(position.xCoord, position.yCoord, position.zCoord, this.textureU(ico, side), this.textureV(ico, side));
		}

		public void drawWithUV(Tessellator v5, ForgeDirection side, double u, double v) {
			v5.addVertexWithUV(position.xCoord, position.yCoord, position.zCoord, u, v);
		}

		public Vec3 getOffsetFromCenter() {
			Vec3 v = this.parent().getCenter();
			Vec3 v2 = position.subtract(v);
			return v2;
		}

		public void offset(double x, double y, double z) {
			position.xCoord += x;
			position.yCoord += y;
			position.zCoord += z;
		}

		public void setPosition(CubeVertex cv) {
			this.setPosition(cv.position.xCoord, cv.position.yCoord, cv.position.zCoord);
		}

		public void setPosition(double x, double y, double z) {
			position.xCoord = x;
			position.yCoord = y;
			position.zCoord = z;
		}

		private CubePoints parent() {
			return CubePoints.this;
		}

	}

	public static CubePoints fullBlock() {
		return new CubePoints(Vec3.createVectorHelper(0, 0, 0), Vec3.createVectorHelper(1, 0, 0), Vec3.createVectorHelper(0, 0, 1), Vec3.createVectorHelper(1, 0, 1), Vec3.createVectorHelper(0, 1, 0), Vec3.createVectorHelper(1, 1, 0), Vec3.createVectorHelper(0, 1, 1), Vec3.createVectorHelper(1, 1, 1));
	}

	public void renderIconOnSides(IBlockAccess world, int x, int y, int z, IIcon ico, Tessellator v5) {
		v5.addTranslation(x, y, z);

		double u = ico.getMinU();
		double du = ico.getMaxU();
		double v = ico.getMinV();
		double dv = ico.getMaxV();

		ForgeDirection dir = ForgeDirection.DOWN;
		x1y1z1.drawWithUV(v5, dir, u, v);
		x2y1z1.drawWithUV(v5, dir, du, v);
		x2y1z2.drawWithUV(v5, dir, du, dv);
		x1y1z2.drawWithUV(v5, dir, u, dv);

		dir = ForgeDirection.UP;
		x1y2z2.drawWithUV(v5, dir, u, dv);
		x2y2z2.drawWithUV(v5, dir, du, dv);
		x2y2z1.drawWithUV(v5, dir, du, v);
		x1y2z1.drawWithUV(v5, dir, u, v);

		dir = ForgeDirection.WEST;
		x1y1z2.drawWithUV(v5, dir, u, dv);
		x1y2z2.drawWithUV(v5, dir, du, dv);
		x1y2z1.drawWithUV(v5, dir, du, v);
		x1y1z1.drawWithUV(v5, dir, u, v);

		dir = ForgeDirection.EAST;
		x2y1z1.drawWithUV(v5, dir, u, v);
		x2y2z1.drawWithUV(v5, dir, du, v);
		x2y2z2.drawWithUV(v5, dir, du, dv);
		x2y1z2.drawWithUV(v5, dir, u, dv);

		dir = ForgeDirection.NORTH;
		x1y1z1.drawWithUV(v5, dir, u, v);
		x1y2z1.drawWithUV(v5, dir, u, dv);
		x2y2z1.drawWithUV(v5, dir, du, dv);
		x2y1z1.drawWithUV(v5, dir, du, v);

		dir = ForgeDirection.SOUTH;
		x2y1z2.drawWithUV(v5, dir, du, v);
		x2y2z2.drawWithUV(v5, dir, du, dv);
		x1y2z2.drawWithUV(v5, dir, u, dv);
		x1y1z2.drawWithUV(v5, dir, u, v);

		v5.addTranslation(-x, -y, -z);
	}

}
