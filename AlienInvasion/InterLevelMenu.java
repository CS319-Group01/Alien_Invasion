public class InterLevelMenu implements Commons
{
	
	private Board board;
	
	public InterLevelMenu(Board board)
	{
		this.board = board;
	}
	
	public void buyAD()
	{
		if(board.getCoins()>=AD_COST)
		{
			board.setCoins(board.getCoins()-AD_COST);
			board.setAttackDamage(board.getAttackDamage()+1);
		}
	}
	
	public void buyAS()
	{
		if(board.getCoins()>=AS_COST)
		{
			board.setCoins(board.getCoins()-AS_COST);
			board.setMissileSpeed(board.getMissileSpeed()+1);
		}
	}
	
	public void buyS()
	{
		if(board.getCoins()>=S_COST)
		{
			board.setCoins(board.getCoins()-S_COST);
			board.setPlayerSpeed(board.getPlayerSpeed()+1);
		}
	}
}