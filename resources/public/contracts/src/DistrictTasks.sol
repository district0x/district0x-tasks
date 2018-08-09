pragma solidity ^0.4.24;

import "./Ownable.sol";

/*
    Only Owner of contract can add and update Tasks.
    Everybody can add Bids to Tasks.
    Everybody can Vote once on Bid.
*/

contract DistrictTasks is Ownable {
    struct Bid {
        address creator;
        address[] voters;
        mapping(address => bool) voted;
    }

    struct Task {
        uint biddingEndsOn;
        bool isActive;
        Bid[] bids;
    }

    Task[] public tasks;


    event LogAddTask (
        uint indexed id,
        string title,
        bool isActive,
        uint biddingEndsOn
    );

    event LogAddBid (
        uint indexed taskId,
        uint indexed bidId,
        address creator
    );

    event LogAddVoter (
        uint indexed taskId,
        uint indexed bidId,
        address voter
    );
    
    event LogUpdateActive(uint indexed id, bool isActive);
    
    event LogUpdateBiddingEndsOn(uint indexed id, uint biddingEndsOn);


    modifier notEmptyString(string s) {
        require(bytes(s).length > 0, "String can't be empty");
        _;
    }

    modifier validBiddingEndsOn(uint biddingEndsOn) {
        require(biddingEndsOn > block.timestamp, "biddingEndsOn > now failed, timestamp expired");
        _;
    }

    modifier activeTask(uint taskId) {
        require(tasks[taskId].isActive == true, "Task is set as not active by Onwer of the contract");
        _;
    }

    modifier voterDidntVote(uint taskId, uint bidId) {
        require(tasks[taskId].bids[bidId].voted[msg.sender] != true, "voter already voted");
        _;
    }

    // tasks

    function addTask(string _title, uint _biddingEndsOn, bool _isActive)
    public
    onlyOwner
    notEmptyString(_title)
    validBiddingEndsOn(_biddingEndsOn)
    {
        uint _taskId = tasks.length++;
        tasks[_taskId].biddingEndsOn = _biddingEndsOn;
        tasks[_taskId].isActive = _isActive;
        emit LogAddTask(
            _taskId,
            _title,
            _isActive,
            _biddingEndsOn
        );
    }

    function updateActive(uint _id, bool _isActive) public onlyOwner {
        tasks[_id].isActive = _isActive;
        emit LogUpdateActive(_id, _isActive);
    }

    function updateBiddingEndsOn(uint _taskId, uint _biddingEndsOn)
    public
    onlyOwner
    validBiddingEndsOn(_biddingEndsOn)
    {
        tasks[_taskId].biddingEndsOn = _biddingEndsOn;
        emit LogUpdateBiddingEndsOn(_taskId, _biddingEndsOn);
    }

    function countTasks() public view returns (uint){
        return tasks.length;
    }

    // bids

    function addBid(uint _taskId) 
    public
    activeTask(_taskId)
    validBiddingEndsOn(tasks[_taskId].biddingEndsOn) {
        uint _bidId = tasks[_taskId].bids.length++;
        tasks[_taskId].bids[_bidId].creator = msg.sender;
        emit LogAddBid(_taskId, _bidId, msg.sender);
    }

    function countBids(uint _taskId) public view returns (uint){
        return tasks[_taskId].bids.length;
    }

    // function getBid(uint _taskId, uint _bidId) public view returns (uint, address[]) {
    //     Bid storage bid = tasks[_taskId].bids[_bidId];
    //     return(bid.creator, bid.voters);
    // }

    // voters

    function addVoter(uint _taskId, uint _bidId)
    public
    activeTask(_taskId)
    validBiddingEndsOn(tasks[_taskId].biddingEndsOn)
    voterDidntVote(_taskId, _bidId)
    {
        tasks[_taskId].bids[_bidId].voters.push(msg.sender);
        tasks[_taskId].bids[_bidId].voted[msg.sender] = true;
        emit LogAddVoter(_taskId, _bidId, msg.sender);
    }

    function countVoters(uint _taskId, uint _bidId) public view returns (uint){
        return tasks[_taskId].bids[_bidId].voters.length;
    }

    function isVoted(uint _taskId, uint _bidId, address _voter) public view returns (bool) {
        return (tasks[_taskId].bids[_bidId].voted[_voter] == true);
    }
}
