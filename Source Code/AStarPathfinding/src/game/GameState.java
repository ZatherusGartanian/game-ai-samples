package game;
/*
 * WEREWOLF SURVIVORS GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     March 27
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * GameState Enumerator
 * Description:
 * Basic enumerator used for switching game states.
 *
 * Future Updates/Refactor:
 * Rarely used in the current build. Also, unsure with the current scene structure. The "GAME" state may need its own enum
 * to monitor individual game states. This is technically scene state atm.
 *
 */
public enum GameState {
    MENU, GAME, GAMEOVER
}