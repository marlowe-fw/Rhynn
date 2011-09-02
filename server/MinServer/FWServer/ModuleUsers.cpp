#include "ModuleUsers.h"
#include "FWServer.h"
#include "FWClient.h"
#include "world_objects/User.h"
#include "world_objects/Playfield.h"
#include "FWWorld.h"
#include "sha1.h"
#include "Random.h"
#include "Security.h"

#include "messages/MsgGameUserRegisterRequest.h"
#include "messages/MsgGameUserRegisterResult.h"
#include "messages/MsgGameUserRegisterRequest.h"
#include "messages/MsgGameUserRegisterResult.h"
#include "messages/MsgGameUserEmailChangeRequest.h"
#include "messages/MsgGameUserEmailChangeResult.h"
#include "messages/MsgGameUserLoginRequest.h"
#include "messages/MsgGameUserLoginResult.h"
#include "messages/MsgGameUserChallengenumber.h"
#include "messages/MsgGameUserPasswordResetCodeRequest.h"
#include "messages/MsgGameUserPasswordResetCodeResult.h"
#include "messages/MsgGameUserPasswordResetNewRequest.h"
#include "messages/MsgGameUserPasswordResetNewResult.h"
#include "messages/MsgGameUserGetEmailRequest.h"
#include "messages/MsgGameUserGetEmailResult.h"
#include "messages/MsgGameCharacterRemove.h"
#include "messages/MsgGameUserForcedLogout.h"

#include <vector>
#include <boost/algorithm/string.hpp>

using namespace fws;
using namespace fwworld;
using namespace fwutil;


ModuleUsers::ModuleUsers(FWServer& newServer, FWWorld& newWorld)
: server(newServer), world(newWorld)
{}

// %%GENERATOR_START%%HANDLER_IMPL%%


/**
* Client registers a user (co).
*/
bool ModuleUsers::handleMessageGameUserRegisterRequest(FWClient* pCurClient, MsgGameUserRegisterRequest& msgObj) {
	// note: user cannot be logged in at this point
	if (pCurClient->hasUser()) {
		server.removeClient(pCurClient, true);
		return false;
	}

	MsgGameUserRegisterResult returnMsg;
	returnMsg.success = 0;
	returnMsg.infoMessage = "Error. Try another username and password."; // generic error message

	// check valid format
	if (User::validName(msgObj.userName) && User::validPassword(msgObj.userPassword)) {
		// check name not already taken

		unsigned int existingId = User::getIdForName(world, msgObj.userName);
		if (existingId == 0) {
			// user does not exist yet, set the password to the sha1 hash and then insert
			std::string hashedPassword = SHA1::hexhashFromString(msgObj.userPassword);
			if (!hashedPassword.empty()) {
				// hashed pass ok, store now
				// we just need the user to store it to the db, so this is a stack object
				User newUser(world);	// constructor sets default values
				newUser.setName(msgObj.userName);
				newUser.setPassword(hashedPassword);	// note that we set the hashed password, we never store plain text passwords
				newUser.setRegisteredDate(DateTime(true));

				if (newUser.storeToDB()) {
					// everything worked, inform user of success
					returnMsg.success = 1;
					returnMsg.infoMessage = "Congratulations!\nYour account has been created!";
				} else {
					std::cout << "could not store user." << std::endl;
				}

			}
		} else {
			// user name already taken
			std::cout << "existing id: " << existingId << std::endl;
			returnMsg.infoMessage = "User name already taken. Try another user name.";
		}
	} else {
		// usually, invalid input is already filtered on the client, so we just need a generic message here for the hackers
		returnMsg.infoMessage = "User name or password not valid.";
	}

	// send the register result message
	server.queueMessageForSending(returnMsg, pCurClient, true);
	return true;
}



/**
* Request client login (co).
*/
bool ModuleUsers::handleMessageGameUserLoginRequest(FWClient* pCurClient, MsgGameUserLoginRequest& msgObj) {

	// todo: test / rework, see below

	// todo: log every failed login attempt
	// todo: allow repeated log-ins only n-times, then time-out or use a temporary ban


	if (pCurClient->hasUser()) {	// user cannot be logged in already
		server.handleClientError(pCurClient, "handleMessageGameVersionRequest");
		return false;
	}

	MsgGameUserLoginResult returnMsg;
	returnMsg.success = 0;
	returnMsg.userId = 0;
	returnMsg.counterValue = 0;
	returnMsg.infoMessage = "Login failed."; // generic error message

    std::cout << "checking valid name / pass" << std::endl;

	//check valid format
	if (User::validName(msgObj.userName) && User::validPassword(msgObj.userPassword)) {
		std::string hashedPassword = SHA1::hexhashFromString(msgObj.userPassword);
		if (!hashedPassword.empty()) {

			// check user exists
			unsigned int existingId = User::getIdForNameAndPassword(world, msgObj.userName, hashedPassword);
			if (existingId != 0) {
				// check double login
				User* alreadyLoggedInUser = world.getLiveUser(existingId);
				if (alreadyLoggedInUser != 0) {
					MsgGameUserForcedLogout	forceLogoutMsg;
					forceLogoutMsg.infoMessage = "Somebody else has logged in with your account!";
					FWClient* otherClient = alreadyLoggedInUser->getClient();
					server.queueMessageForSending(forceLogoutMsg, otherClient, true);
					server.removeClient(otherClient, true);
					//returnMsg.infoMessage = "User is already logged in.";
				}


				// user exists and is not logged in yet
				std::string hashedPassword = SHA1::hexhashFromString(msgObj.userPassword);
				if (!hashedPassword.empty()) {
					// load user
					SPUser u(_TRACK_NEW(new User(world)));

					if (u->loadFromDB(existingId)) {

						// TODO: check user type, etc.
						if (u->getUserSystemStatus() != UserSystemStatus::active) {
							returnMsg.infoMessage = "Account inactive or banned.\nIf you believe this is a mistake, you may want to contact the Rhynn team, see www.rhynn.com.";
						} else {
							// login successful!
							// todo: check premium status expired and flag accordingly if required!
							// $-> important!
							returnMsg.success = 1;
							returnMsg.infoMessage = "";
							returnMsg.userId = u->getId();

							/*
							get actual random counter value
							using a counter from here onwards means that the client will need to correctly send the message packet-check signature
							reverse engineering the packet signatures is a tough task but can be made harder by adjusting the counter value in regular intervals by transmitting new counters from the server to the client
							however, hackers will try to trigger the whole send function, so there must be an additional dynamic element on the client to prevent that
							*/
							int newCounter = Random::nextInt(64000) + 112;

							// obfuscate counter before sending by using a fixed xor mask
							// todo: could use part of the challenge number for further obfuscation
							int sendCounter = newCounter ^ 456971801;
							returnMsg.counterValue = sendCounter;

							// create and set the crypted challenge number which the client must decrypt and send back with the next message
							std::string challengeNumber = Security::createChallengeNumber(8);
							unsigned char sendChallengeNumber[8];
							min::NetPort::stringToBytes(challengeNumber, sendChallengeNumber);
							Security::simpleCrypt(sendChallengeNumber, 8);
							returnMsg.challengeNumberData.fromBuffer(sendChallengeNumber, 8);

							// execute common code
							server.onSuccessfulLogin(u, pCurClient, challengeNumber, newCounter);

							std::cout << world.clock.getTimestampStr() << " User logged in, client: " << pCurClient->getClientSocket() << " user: " << u->getId() << std::endl;

						}
					}
				}


			} else {
				// Login failed
				//std::cout << "Login failed: " << existingId << std::endl;
			}
		}
	} else {
		// usually, invalid input is already filtered on the client, so we just need a generic message here for the hackers
		returnMsg.infoMessage = "User name or password not valid.";
	}

	// send the login result message
	server.queueMessageForSending(returnMsg, pCurClient, true);

	return true;
}


/**
* Receive the encrypted challenge number to validate the client (co).
*/
bool ModuleUsers::handleMessageGameUserChallengenumber(FWClient* pCurClient, MsgGameUserChallengenumber& msgObj) {
	if (!pCurClient->hasUser()) {
		server.handleClientError(pCurClient, "handleMessageGameUserChallengenumber");
		return false;
	}

	// compare challenge number to the number that was set on login
	User* u = pCurClient->getUser();

	if (u->getChallengeNumber() == msgObj.challengeNumber.str()) {
		// challenge number was correctly decrypted and sent back by the client
		// from here onwards the user is considered to be validated
		u->onValidationCompleted();
	} else {
		server.removeClient(pCurClient, true);
	}
	return true;
}


/**
* Client requests to receive the current e-mail address (co).
*/
bool ModuleUsers::handleMessageGameUserGetEmailRequest(FWClient* pCurClient, MsgGameUserGetEmailRequest& msgObj) {
	if (!msgObj.isValid() || !pCurClient->hasValidatedUser()) {
		server.handleClientError(pCurClient, "handleMessageGameUserGetEmailRequest");
		return false;
	}

	MsgGameUserGetEmailResult returnMsg;

	User* u = pCurClient->getUser();

	std::string email = u->getEmail();

	if (!email.empty()) {
		std::vector<std::string> emailParts;
		//fwutil::Common::tokenize(email, emailParts, "@");
		boost::split(emailParts, email, boost::is_any_of("@"));
		if (emailParts.size() == 2) {
			returnMsg.firstPart = emailParts[0];
			returnMsg.secondPart = emailParts[1];
		}
	}

	server.queueMessageForSending(returnMsg, pCurClient, true);

	return true;
}

/**
* Client requests to change the e-mail address (co).
*/
bool ModuleUsers::handleMessageGameUserEmailChangeRequest(FWClient* pCurClient, MsgGameUserEmailChangeRequest& msgObj) {
	if (!pCurClient->hasValidatedUser()) {
		server.handleClientError(pCurClient, "handleMessageGameUserEmailChangeRequest");
		return false;
	}

	// message contains first and second part of the e-mail (before the @ and after the @) - reassemble
	std::stringstream ssEmail;
	ssEmail << msgObj.firstPart << "@" << msgObj.secondPart;
	std::string email = ssEmail.str();

	MsgGameUserEmailChangeResult returnMsg;
	returnMsg.success = 0;

	// check for validity of the e-mail format
	if (User::validEmail(email)) {
		User* curUser = pCurClient->getUser();

		// check if e-mail not used by somebody else
		unsigned int existingId = User::getIdForEmail(world, email);
		if (existingId > 0 && existingId != curUser->getId()) {
			// e-mail already taken by somebody else
			returnMsg.infoMessage = "E-mail is already taken by another user.";
		} else {
			// everything ok, change e-mail
			curUser->getUserSession().logKeyAction("e-mail changed from " + curUser->getEmail() + " to " + email);
			curUser->setEmail(email, true);

			returnMsg.infoMessage = "Your e-mail has been changed.";
			returnMsg.success = 1;
		}
	} else {
		returnMsg.infoMessage = "E-mail is not valid, please provide a correct e-mail address or skip this step.";
	}

	server.queueMessageForSending(returnMsg, pCurClient, true);

	return true;
}

/**
 * User is requesting that a reset code be sent to his e-mail address, provided the user has entered a correct e-mail address for his account (co).
 * Note that actual sending of the reset code is done by an external process (usually cron job calling a script every minute)
 */
bool ModuleUsers::handleMessageGameUserPasswordResetCodeRequest(FWClient* pCurClient, MsgGameUserPasswordResetCodeRequest& msgObj) {
	// !! note the user must NOT be logged in to send this message, if the user is logged in, then this indicates a hacking attempt
	if (pCurClient->hasUser()) {
		server.handleClientError(pCurClient, "handleMessageGameUserPasswordResetCodeRequest");
		return false;
	}

	MsgGameUserPasswordResetCodeResult returnMsg;
	returnMsg.success = 0;

	// try to get user for name, also make sure user status is active
	unsigned int userId = User::getIdForName(world, msgObj.name);
	if (userId > 0) {
		User u(world);
		if (u.loadFromDB(userId) && u.getUserSystemStatus() == UserSystemStatus::active) {
			// check if the email address is valid
			if (User::validEmail(u.getEmail())) {
				// e-mail is valid, so try to insert a new reset code entry
				if (server.setNewPasswordResetCodeForUser(u)) {
					// everything worked well, new reset code was inserted
					returnMsg.success = 1;
				}
			} else {
				returnMsg.infoMessage = "No valid e-mail address was provided for this account.";
			}
		}
	}

	if (returnMsg.success != 1 && returnMsg.infoMessage.empty()) {
		// some generic error
		returnMsg.infoMessage = "Reset-code could not be requested for this user.";
	}

	server.queueMessageForSending(returnMsg, pCurClient, true);
	return true;
}

/**
 * User is setting a new password for his account using the reset code that he has been sent via e-mail (co).
 */
bool ModuleUsers::handleMessageGameUserPasswordResetNewRequest(FWClient* pCurClient, MsgGameUserPasswordResetNewRequest& msgObj) {
	// !! note the user must NOT be logged in to send this message, if the user is logged in, then this indicates a hacking attempt
	if (pCurClient->hasUser()) {
		server.handleClientError(pCurClient, "handleMessageGameUserPasswordResetNewRequest");
		return false;
	}

	MsgGameUserPasswordResetNewResult returnMsg;
	returnMsg.success = 0;

	// try to get user for name, also make sure user status is active
	unsigned int userId = User::getIdForName(world, msgObj.name);
	if (userId > 0) {
		User u(world);
		if (u.loadFromDB(userId) && u.getUserSystemStatus() == UserSystemStatus::active) {
			boost::trim(msgObj.password);
			if (User::validPassword(msgObj.password)) {
				std::string hashedNewPass = fwutil::SHA1::hexhashFromString(msgObj.password);
				if (server.resetPasswordForUser(u, msgObj.resetCode, hashedNewPass)) {
					returnMsg.success = 1;
				} else {
					returnMsg.infoMessage = "Reset-code does not match. Did you request and receive a reset-code?";
				}
			} else {
				// invalid password, just remove this client (as checks are already in place on the client app)
				server.removeClient(pCurClient, true);
				return false;
			}
		}
	}

	if (returnMsg.success != 1 && returnMsg.infoMessage.empty()) {
		// generic error
		returnMsg.infoMessage = "Password could not be changed";
	}

	server.queueMessageForSending(returnMsg, pCurClient, true);


	return true;
}

// %%GENERATOR_END%%HANDLER_IMPL%%

void ModuleUsers::removeUser(User* u) {
	UserSession& session = u->getUserSession();
	session.endSession();
	session.storeToDB();

	std::cout << world.clock.getTimestampStr() << " Removing user: " << u->getId() << std::endl;

	if (u->getIsValidated()) {
		u->storeToDBIfNotSynchronized();
		if (u->hasSelectedCharacter()) {
			//c->removeFromWorld();
			server.getModuleCharacters()->removeCharacterFromWorld(u->getSelectedCharacter());
		}
	}

	world.removeLiveUser(u->getId());
}