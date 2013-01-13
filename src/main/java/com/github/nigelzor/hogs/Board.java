package com.github.nigelzor.hogs;

import java.io.IOException;
import java.util.List;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class Board {
	private static final int PLAYERS = 4, ROWS = 4, COLS = 4;

	private int currentPlayer = 0;
	private List<Player> players;
	private List<Home> homes;
	private ShiftMatrix<Tile> tiles;

	public Board() {
		players = Lists.newArrayList();
		homes = Lists.newArrayList();
		for (int i = 0; i < PLAYERS; i++) {
			Colour colour = Colour.values()[i];
			Player player = new Player(colour);
			Home home = new Home(colour);
			home.getPlayers().add(player);
			players.add(player);
			homes.add(home);
		}

		tiles = new ShiftMatrix<Tile>(ROWS, COLS);

		tiles.set(0, 0, TileFactory.tee());
		tiles.set(1, 0, TileFactory.straight().rotate(Rotation.NINETY_DEGREES));
		tiles.set(2, 0, TileFactory.elbow().rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES));

		tiles.set(0, 3, TileFactory.tee().rotate(Rotation.NINETY_DEGREES));
		tiles.set(0, 2, TileFactory.straight());
		tiles.set(0, 1, TileFactory.elbow().rotate(Rotation.TWO_HUNDRED_SEVENTY_DEGREES));

		tiles.set(3, 3, TileFactory.tee().rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES));
		tiles.set(2, 3, TileFactory.straight().rotate(Rotation.NINETY_DEGREES));
		tiles.set(1, 3, TileFactory.elbow());

		tiles.set(3, 0, TileFactory.tee().rotate(Rotation.TWO_HUNDRED_SEVENTY_DEGREES));
		tiles.set(3, 1, TileFactory.straight());
		tiles.set(3, 2, TileFactory.elbow().rotate(Rotation.NINETY_DEGREES));

		tiles.set(1, 1, TileFactory.tower().rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES));
		tiles.set(1, 2, TileFactory.homework());
		tiles.set(2, 2, TileFactory.potions());
		tiles.set(2, 1, TileFactory.creatures().rotate(Rotation.TWO_HUNDRED_SEVENTY_DEGREES));
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(players, homes, tiles);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Board other = (Board) obj;
		return Objects.equal(players, other.players)
				&& Objects.equal(homes, other.homes)
				&& Objects.equal(tiles, other.tiles);
	}

	public void print(Appendable out) throws IOException {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (tiles.get(row, col) == null) {
					out.append("   ");
				} else {
					out.append(' ').append(tiles.get(row, col).connectsTo(Direction.NORTH) ? 'X' : ' ').append(' ');
				}
			}
			out.append("\n");
			for (int col = 0; col < COLS; col++) {
				if (tiles.get(row, col) == null) {
					out.append(" / ");
				} else {
					out.append(tiles.get(row, col).connectsTo(Direction.WEST) ? 'X' : ' ');
					out.append(tiles.get(row, col).getObjective().isPresent() ? 'G' : 'X');
					out.append(tiles.get(row, col).connectsTo(Direction.EAST) ? 'X' : ' ');
				}
			}
			out.append("\n");
			for (int col = 0; col < COLS; col++) {
				if (tiles.get(row, col) == null) {
					out.append("   ");
				} else {
					out.append(' ').append(tiles.get(row, col).connectsTo(Direction.SOUTH) ? 'X' : ' ').append(' ');
				}
			}
			out.append("\n");
		}
	}

}
