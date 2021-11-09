import * as React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

import { Tooltip } from '@material-ui/core';
import {APIPath, APIParameters} from '../../config/ApiConfig';
import {signIn, submitPost} from '../../actions/User';
import { useSnackbar } from 'notistack';

export default function FormDialog({message, title, accessToken, accessTokenSecret, setAccessToken, setAccessTokenSecret}: {message:string, title: string, accessToken: string, accessTokenSecret: string,  setAccessToken: React.Dispatch<React.SetStateAction<string>>, setAccessTokenSecret: React.Dispatch<React.SetStateAction<string>>}) {
  const [open, setOpen] = React.useState(false);
  const [signInText, setSignInText] = React.useState("Sign in");
  const { enqueueSnackbar} = useSnackbar();
  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const submitDiscordPost = async () => {
    const query = {
      [APIParameters.ACCESS_TOKEN]: accessToken,
      [APIParameters.ACCESS_TOKEN_SECRET]: accessTokenSecret,
      [APIParameters.TITLE]: title,
      [APIParameters.MESSAGE]: message,}
    await submitPost({url: APIPath.SUBMIT_DISCORD, query, enqueueSnackbar,});
  }
  const handleSignIn =  async () => {
    await signIn({setSignInText, url: APIPath.SIGN_IN_DISCORD, enqueueSnackbar, setAccessToken, setAccessTokenSecret});
  }
  return (
    <div>
      <Button variant="contained" onClick={handleClickOpen} style={accessToken ? {backgroundColor:"#7289DA",
            color: "white"} : {}}>
        Discord
      </Button>
      <Dialog fullWidth maxWidth={"sm"} open={open} onClose={handleClose}>
        <DialogTitle>Discord</DialogTitle>
        <DialogContent>
          <DialogContentText>
          {!accessToken && "To post your message to Discord please sign-in to your Discord account."}
          </DialogContentText>
          <Button onClick={() => handleSignIn()} style={{
            backgroundColor:"#7289DA",
            color:"white",
          }}> {signInText} </Button>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Tooltip title="Text and signing in are required">
          <div>
              <Button 
              variant= "contained"
              color="primary" 
              type="submit"
              onClick={submitDiscordPost}
              disabled={(message.length === 0 || accessToken.length === 0 )}
              >
                Submit
              </Button>
            </div>
          </Tooltip>
        </DialogActions>
      </Dialog>
    </div>
  );
}