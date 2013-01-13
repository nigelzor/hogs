package com.github.nigelzor.hogs;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Board {
	private static final int PLAYERS = 4, ROWS = 4, COLS = 4;

	private int currentPlayer = 0;
	private List<Player> players;
	private List<Home> homes;
	private List<HomeConnection> homeConnections;
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

		homeConnections = Lists.newArrayList();
		homeConnections.add(new HomeConnection(0, 0, Direction.SOUTH, Direction.EAST));
		homeConnections.add(new HomeConnection(0, COLS - 1, Direction.SOUTH, Direction.WEST));
		homeConnections.add(new HomeConnection(ROWS - 1, COLS - 1, Direction.NORTH, Direction.WEST));
		homeConnections.add(new HomeConnection(ROWS - 1, 0, Direction.NORTH, Direction.EAST));

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

	// optional because a player could also be in a Home
	private Optional<Index> findPlayerTile(Player player) {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile tile = tiles.get(row, col);
				if (tile != null && tile.getPlayers().contains(player)) {
					return Optional.of(new Index(row, col));
				}
			}
		}
		return Optional.absent();
	}

	public Set<Move> potentialMoves() {
		Set<Move> options = Sets.newHashSet();

		options.add(new NoMove());

		Player player = players.get(currentPlayer);
		addWalkMoves(options, player);

		return options;
	}

	private void addWalkMoves(Set<Move> options, Player player) {
		for (Home home : homes) {
			if (home.getPlayers().contains(player)) {
				HomeConnection homeConnection = homeConnections.get(currentPlayer);
				Tile connecting = tiles.get(homeConnection.getRow(), homeConnection.getCol());
				for (Direction direction : homeConnection.getEdges()) {
					if (connecting.connectsTo(direction.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES))) {
						options.add(new WalkMove(player, direction, home, connecting));
					}
				}
				return;
			}
		}
		Index index = findPlayerTile(player).get(); // where is the player, if not in a home or a tile?
		Tile tile = tiles.get(index.getRow(), index.getCol());
		for (Direction direction : tile.getConnections()) {
			Index connectedIndex = direction.apply(index);
			if (valid(connectedIndex)) {
				Tile connectedTile = tiles.get(connectedIndex.getRow(), connectedIndex.getCol());
				if (connectedTile.connectsTo(direction.rotate(Rotation.ONE_HUNDRED_EIGHTY_DEGREES))) {
					options.add(new WalkMove(player, direction, tile, connectedTile));
				}
			}
		}
	}

	private boolean valid(Index index) {
		return index.getRow() >= 0 && index.getRow() < ROWS
				&& index.getCol() >= 0 && index.getCol() < COLS;
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
