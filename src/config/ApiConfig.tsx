const APIbasePath = "http://localhost:1338";
type APIPathType = {
  SIGN_IN_REDDIT: string,
  SIGN_IN_TWITTER: string,
  SUBMIT_REDDIT: string,
};
type APIParametersType = {
  ACCESS_TOKEN: string,
  TITLE: string,
  MESSAGE: string,
};
type SubmitResponseKeysType = {
  SUCCESS_STATUS:string,
  MESSAGE: string,
}
const APIPath: APIPathType = Object.freeze({
  SIGN_IN_REDDIT: APIbasePath + "/redditsignin",
  SIGN_IN_TWITTER: APIbasePath + "/twittersignin",
  SUBMIT_REDDIT: APIbasePath + "/redditsubmitpost",
});
const APIParameters: APIParametersType = Object.freeze({
  ACCESS_TOKEN: "access_token",
  TITLE: "title",
  MESSAGE: "message",
});
const SubmitResponseKeys: SubmitResponseKeysType = Object.freeze({
  SUCCESS_STATUS: "success_status",
  MESSAGE: "message",
});
const ErrorResponseKey = Object.freeze({
  ERROR: "error",
})

export { APIPath, APIParameters, SubmitResponseKeys, ErrorResponseKey };
