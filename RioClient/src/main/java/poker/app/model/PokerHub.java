package poker.app.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import exceptions.DeckException;
import netgame.common.Hub;
import pokerBase.Action;
import pokerBase.Card;
import pokerBase.CardDraw;
import pokerBase.Deck;
import pokerBase.GamePlay;
import pokerBase.GamePlayPlayerHand;
import pokerBase.Player;
import pokerBase.Rule;
import pokerBase.Table;
import pokerEnums.eAction;
import pokerEnums.eCardDestination;
import pokerEnums.eDrawCount;
import pokerEnums.eGame;
import pokerEnums.eGameState;

public class PokerHub extends Hub {

	private Table HubPokerTable = new Table();
	private GamePlay HubGamePlay;
	private int iDealNbr = 0;
	private eGameState eGameState;

	public PokerHub(int port) throws IOException {
		super(port);
	}

	protected void playerConnected(int playerID) {

		if (playerID == 2) {
			shutdownServerSocket();
		}
	}

	protected void playerDisconnected(int playerID) {
		shutDownHub();
	}

	protected void messageReceived(int ClientID, Object message) {

		if (message instanceof Action) {
			Player actPlayer = (Player) ((Action) message).getPlayer();
			Action act = (Action)message;
			switch (act.getAction())
			{
			case Sit:
				HubPokerTable.AddPlayerToTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);				
				break;
			case Leave:
				HubPokerTable.RemovePlayerFromTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);				
				break;
			case TableState:
				resetOutput();
				sendToAll(HubPokerTable);				
				break;
			case StartGame:
				
				Rule rle = new Rule(act.geteGame());
				
				Iterator<Entry<UUID,Player>> random = HubGamePlay.getGamePlayers().entrySet().iterator();
				int randomNumber = (int) Math.random() * 4;            
				UUID dealerUUID = null;
				for(int i = 0; i < randomNumber; i++)
						dealerUUID = random.next().getKey(); 
				
				HubGamePlay = new GamePlay(rle, dealerUUID);
			
				HubGamePlay.setGamePlayers(HubPokerTable.getHashPlayers());
				
				HubGamePlay.setGameDeck(new Deck(rle.GetNumberOfJokers(),rle.GetWildCards()));
				//MAKE SURE DECK CONSTRUCTOR CAN HANDLE AN EMPTY ARRAY OF WILDCARDS
				
				//	TODO: 
			case Draw:
				
				//	TODO: Draw cards based on next in hmCardDraw
				//			You might have to draw two cards, one card, three cards
				
				//			You might have to add cards to player(s) hands, community

				//	TODO: Update eDrawCountLast in GamePlay.  This attribute will 
				//		tell the client what card(s) need to be dealt to which players.
				
				resetOutput();
				sendToAll(HubGamePlay);	
				break;
			}			
		}

	}

}
