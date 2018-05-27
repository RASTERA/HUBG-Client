public class Enemy {

	private final String name;
	private float x, y, vx, vy, rotation, rotationVelocity;
	private final int ID;

	public Enemy(String name, float[] info) {
		this.name = name;
		this.x = info[0];
		this.y = info[1];
		this.rotation = info[2];
		this.ID = (int) info[3];
	}

	public void update(float[] newLocation) {
		this.x = newLocation[0];
		this.y = newLocation[1];
		this.rotation = newLocation[2];
	}

	public int getID() {
		return this.ID;
	}
}
