import * as React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

import {APIPath, APIParameters} from '../../config/ApiConfig';
import { useSnackbar } from 'notistack';
import {signIn, submitPost} from '../../actions/User';
import { Tooltip } from '@material-ui/core';

export default function TwitterForm({message, title}: {message:string, title: string}) {
  const [open, setOpen] = React.useState(false);
  const [signInText, setSignInText] = React.useState("Sign in");
  const { enqueueSnackbar} = useSnackbar();
  const [accessToken, setAccessToken] = React.useState(null);
  const [accessTokenSecret, setAccessTokenSecret] = React.useState(null);
  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };
  const submitTwitterPost = async () => {
    const query = {
      [APIParameters.ACCESS_TOKEN]: accessToken,
      [APIParameters.ACCESS_TOKEN_SECRET]: accessTokenSecret,
      [APIParameters.TITLE]: title,
      [APIParameters.MESSAGE]: message,}
    await submitPost({url: APIPath.SUBMIT_TWITTER, query, enqueueSnackbar,});
    }
  
  const handleSignIn =  async () => {
    await signIn({setSignInText, url: APIPath.SIGN_IN_TWITTER, enqueueSnackbar, setAccessToken, setAccessTokenSecret});
  }
  return (
    <div>
      <Button variant="contained" onClick={handleClickOpen}>
        Twitter
      </Button>
      <Dialog fullWidth maxWidth={"sm"} open={open} onClose={handleClose}>
        <DialogTitle>Twitter</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {!accessToken && "To post your message to Reddit please sign-in to your Twitter account."}
          </DialogContentText>
          <Button onClick={() => handleSignIn()} style={{
            backgroundColor:"#1DA1F2",
            color:"white",
          }}> {signInText} </Button>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Tooltip title="Title, text and signing in are required">
            <div>
              <Button 
              variant= "contained"
              color="primary" 
              type="submit"
              onClick={submitTwitterPost}
              disabled={(title.length === 0 || message.length === 0 || accessToken == null )}
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
