pragma solidity ^0.4.24;

import "./Ownable.sol";

/*
    Only Owner of contract can add and update Tasks.
    Everybody can add Bids to Tasks.
    Everybody can Vote on Bids.
*/

contract DistrictTasks is Ownable {
    struct Bids {
        address creator;
        address[] voters;
    }

    struct Task {
        uint biddingEndsOn;
        bool isActive;
        Bids[] bids;
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


    modifier validBiddingEndsOn(uint biddingEndsOn) {
        require(block.timestamp <= biddingEndsOn, "biddingEndsOn has to be in the future");
        _;
    }

    modifier validTitle(string title) {
        require(bytes(title).length > 0, "Title of the task is required");
        _;
    }

    // tasks

    function addTask(string _title, uint _biddingEndsOn, bool _isActive)
    public
    onlyOwner
    validTitle(_title)
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

    function updateBiddingEndsOn(uint _id, uint _biddingEndsOn)
    public
    onlyOwner
    validBiddingEndsOn(_biddingEndsOn)
    {
        tasks[_id].biddingEndsOn = _biddingEndsOn;
        emit LogUpdateBiddingEndsOn(_id, _biddingEndsOn);
    }

    function countTasks() public view returns (uint){
        return tasks.length;
    }

    // bids

    function addBid(uint _taskId) public {
        uint _bidId = tasks[_taskId].bids.length++;
        tasks[_taskId].bids[_bidId].creator = msg.sender;
        emit LogAddBid(_taskId, _bidId, msg.sender);
    }

    function countBids(uint _taskId) public view returns (uint){
        return tasks[_taskId].bids.length;
    }

    // votes

    function addVoter(uint _taskId, uint _bidId) public {
        tasks[_taskId].bids[_bidId].voters.push(msg.sender);
        emit LogAddVoter(_taskId, _bidId, msg.sender);
    }

    function countVoters(uint _taskId, uint _bidId) public view returns (uint){
        return tasks[_taskId].bids[_bidId].voters.length;
    }
}
