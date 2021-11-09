const APIbasePath = "http://localhost:1338";
type APIPathType = {
  SIGN_IN_REDDIT: string,
  SIGN_IN_TWITTER: string,
  SIGN_IN_DISCORD:string,
  SUBMIT_REDDIT: string,
  SUBMIT_TWITTER: string,
  SUBMIT_DISCORD: string,
  SUBMIT_EMAIL: string,
};
type APIParametersType = {
  ACCESS_TOKEN: string,
  ACCESS_TOKEN_SECRET: string,
  TITLE: string,
  MESSAGE: string,
  EMAIL_USERNAME: string,
  EMAIL_PASSWORD: string,
  DEST_EMAILS: string,
  SMTP_SERVER: string,
  SMTP_PORT: string,
};
type SubmitResponseKeysType = {
  SUCCESS_STATUS:string,
  MESSAGE: string,
}
const APIPath: APIPathType = Object.freeze({
  SIGN_IN_REDDIT: APIbasePath + "/redditsignin",
  SIGN_IN_TWITTER: APIbasePath + "/twittersignin",
  SIGN_IN_DISCORD: APIbasePath + "/discordsignin",
  SUBMIT_REDDIT: APIbasePath + "/redditsubmitpost",
  SUBMIT_TWITTER: APIbasePath + "/twittersubmitpost",
  SUBMIT_DISCORD: APIbasePath + "/discordsubmitpost",
  SUBMIT_EMAIL: APIbasePath + "/emailsubmitpost",
});
const APIParameters: APIParametersType = Object.freeze({
  ACCESS_TOKEN: "access_token",
  ACCESS_TOKEN_SECRET: "access_token_secret",
  TITLE: "title",
  MESSAGE: "message",
  EMAIL_USERNAME: "email_from",
  EMAIL_PASSWORD: "password",
  DEST_EMAILS: "dest_emails",
  SMTP_SERVER: "smtp_server",
  SMTP_PORT: "smtp_port",
});
const SubmitResponseKeys: SubmitResponseKeysType = Object.freeze({
  SUCCESS_STATUS: "success_status",
  MESSAGE: "message",
});
const ErrorResponseKey = Object.freeze({
  ERROR: "error",
})

export { APIPath, APIParameters, SubmitResponseKeys, ErrorResponseKey };
